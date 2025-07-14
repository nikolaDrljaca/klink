import { makePersisted } from "@solid-primitives/storage";
import localforage from "localforage";
import { createStore, produce, unwrap } from "solid-js/store";
import {
  CreateKlinkPayload,
  QueryExistingPayload,
  QueryExistingPayloadKlinksInner,
  UpdateKlinkRequest,
} from "~/generated";
import { klinkEntryForageKey } from "~/lib/klink-utils";
import { makeEncoder } from "~/lib/make-encoder";
import { makeKeyEncoder } from "~/lib/make-key-encoder";
import makeKlinkApi from "~/lib/make-klink-api";
import makeRelativeTime from "~/lib/relative-time";
import { Klink, KlinkEntry, klinkMetadata } from "~/types/domain";

const store = createStore<Record<string, Klink>>({});
const [klinks, setKlinks] = makePersisted(
  store,
  {
    name: "klink-store",
    storage: localforage,
  },
);

const api = makeKlinkApi();
const relativeTime = makeRelativeTime();

// service functions

function copyExistingKlink(id: string) {
  const temp = klinks[id];
  if (!temp) {
    return;
  }
  const copy: Klink = {
    id: crypto.randomUUID(),
    name: `Copy of ${temp.name}`,
    description: temp.description ?? "",
    updatedAt: Date.now(),
    readKey: null,
    writeKey: null,
  };
  setKlinks(copy.id, copy);
}

function createNewKlink(data: { name: string; description?: string }) {
  const klink: Klink = {
    id: crypto.randomUUID(),
    name: data.name,
    description: data.description,
    updatedAt: Date.now(),
    readKey: null,
    writeKey: null,
  };
  setKlinks(klink.id, klink);
}

function createKlink(klink: Klink, entries: { value: string }[]) {
  const existing = new Set(Object.values(klinks).map((it) => it.id));
  if (existing.has(klink.id)) {
    return;
  }
  const entryKey = klinkEntryForageKey(klink.id);
  localforage.setItem(entryKey, JSON.stringify(entries));
  setKlinks(klink.id, klink);
}

async function importKlink(
  klinkId: string,
  encodedKey?: string,
) {
  if (!encodedKey) {
    return Promise.reject();
  }
  if (!klinkId) {
    return Promise.reject();
  }
  const encoder = makeKeyEncoder(makeEncoder());
  const { readKey, writeKey } = encoder.decode(encodedKey);

  return api.getKlink({
    klinkId: klinkId,
    readKey: readKey,
    writeKey: writeKey,
  });
}

async function deleteKlink(id: string, shouldDeleteShared: boolean) {
  const klink = klinks[id];
  if (!klink) {
    return;
  }
  // remove entries from local store
  const forageKey = klinkEntryForageKey(id);
  await localforage.removeItem(forageKey);

  const removeLocal = () => {
    setKlinks(produce((k) => delete k[id]));
    // setKlinks(id, undefined);
  };
  if (!shouldDeleteShared) {
    // remove only local klink
    removeLocal();
    return;
  }
  //  remove local and shared
  await api.deleteKlink({
    klinkId: id,
    readKey: klink.readKey,
    writeKey: klink.writeKey,
  });
  removeLocal();
}

async function editKlink(
  data: { id: string; name: string; description?: string },
) {
  const klink = klinks[data.id];
  if (!klink) {
    return;
  }
  const metadata = klinkMetadata(klink);
  // declare function for local update
  const updateKlink = (
    value: { name: string; description?: string; updatedAt: number },
  ) => {
    const updated: Klink = {
      ...klink,
      name: value.name,
      description: value.description,
      updatedAt: value.updatedAt,
    };
    setKlinks(klink.id, updated);
  };

  // handle update for shared klinks
  if (metadata.isEditable) {
    const payload: UpdateKlinkRequest = {
      klinkId: klink.id,
      readKey: klink.readKey,
      writeKey: klink.writeKey,
      patchKlinkPayload: {
        name: data.name,
        description: data.description,
      },
    };
    const updated = await api.updateKlink(payload);
    updateKlink({
      name: updated.name,
      description: updated.description,
      updatedAt: relativeTime.unixFromResponse(updated.updatedAt),
    });
    return;
  }

  // handle update for local only
  updateKlink({
    name: data.name,
    description: data.description,
    updatedAt: Date.now(),
  });
}

async function shareKlink(id: string) {
  const klink = klinks[id];
  if (!klink) {
    return;
  }

  const entriesRaw: string = await localforage.getItem(
    `klink-items-${klink.id}`,
  );
  const entries: KlinkEntry[] = JSON.parse(entriesRaw ?? "[]");
  const payload: CreateKlinkPayload = {
    name: klink.name,
    id: klink.id,
    entries: entries,
    description: klink.description,
  };
  // store on server
  const response = await api.createKlink({ createKlinkPayload: payload });
  const updated: Klink = {
    ...klink,
    readKey: response.readKey,
    writeKey: response.writeKey,
    updatedAt: relativeTime.unixFromResponse(response.updatedAt),
  };
  setKlinks(klink.id, updated);
}

async function syncKlinks() {
  // local procedure to create request payload
  const allKlinks = Object.values(unwrap(klinks));
  const createPaylaod = (klinks: Klink[]): QueryExistingPayload => {
    const out: QueryExistingPayloadKlinksInner[] = [];
    for (const item of klinks) {
      // filter out local klinks
      if (!item.readKey) {
        continue;
      }
      out.push({
        id: item.id,
        readKey: item.readKey,
      });
    }
    return { klinks: out };
  };
  const payload = createPaylaod(allKlinks);
  // if there are no shared klinks -- skip request
  if (payload.klinks.length === 0) {
    return;
  }
  const data = await api.queryExisting({
    queryExistingPayload: createPaylaod(allKlinks),
  });
  const sharedKlinks = new Map(data.map((it) => [it.id, it]));
  // compute local change
  for (const curr of allKlinks) {
    const item = { ...curr };
    const isShared = sharedKlinks.has(item.id);
    if (isShared) {
      // local klink is still shared -- update its data
      const updated = sharedKlinks.get(item.id);
      item.name = updated.name;
      item.description = updated.description;
      item.updatedAt = relativeTime.unixFromResponse(updated.updatedAt);
    } else {
      // local klink is no longer shared -- delete its keys
      item.readKey = null;
      item.writeKey = null;
    }
    setKlinks(item.id, item);
  }
}

function makeLocal(klinkId: string) {
  const current = klinks[klinkId];
  if (!current) {
    return;
  }
  const updated: Klink = {
    ...current,
    readKey: null,
    writeKey: null,
    updatedAt: Date.now(),
  };
  setKlinks(klinkId, updated);
}

function getKlinkStore(): Record<string, Klink> {
  return klinks;
}

export const KlinkService = {
  copyExistingKlink,
  createKlink,
  createNewKlink,
  deleteKlink,
  editKlink,
  importKlink,
  getKlinkStore,
  makeLocal,
  shareKlink,
  syncKlinks,
};
