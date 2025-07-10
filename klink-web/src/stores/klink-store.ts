import { makePersisted } from "@solid-primitives/storage";
import localforage from "localforage";
import { Accessor, createMemo } from "solid-js";
import { createStore } from "solid-js/store";
import { CreateKlinkPayload, UpdateKlinkRequest } from "~/generated";
import makeKlinkApi from "~/lib/make-klink-api";
import makeRelativeTime from "~/lib/relative-time";
import { Klink, KlinkEntry } from "~/types/domain";

type KlinkStore = Klink /*& { entries: Array<KlinkEntry> }*/;

const store = createStore<Record<string, KlinkStore>>({});
const [klinks, setKlinks] = makePersisted(
  store,
  {
    name: "klink-store",
    storage: localforage,
  },
);

const api = makeKlinkApi();
const relativeTime = makeRelativeTime();

function copyExistingKlink(id: string) {
  const temp = klinks[id];
  if (!temp) {
    return;
  }
  const copy: KlinkStore = {
    id: crypto.randomUUID(),
    name: `Copy of ${temp.name}`,
    description: temp.description ?? "",
    updatedAt: Date.now(),
    readKey: null,
    writeKey: null,
    // entries: [...temp.entries],
  };
  setKlinks(copy.id, copy);
}

function createNewKlink(data: { name: string; description?: string }) {
  const klink: KlinkStore = {
    id: crypto.randomUUID(),
    name: data.name,
    description: data.description,
    updatedAt: Date.now(),
    readKey: null,
    writeKey: null,
    // entries: [],
  };
  setKlinks(klink.id, klink);
}

// TODO: move this utility to lib
const klinkEntryForageKey = (id: string) => `klink-items-${id}`;

// TODO: what to do with this?
const isSharedEditable = (klink: Klink) => !!klink.readKey && !!klink.writeKey;
const isShared = (klink: Klink) => !!klink.readKey;

async function deleteKlink(id: string, shouldDeleteShared: boolean) {
  const klink = klinks[id];
  if (!klink) {
    return;
  }
  // remove entries from local store
  const forageKey = klinkEntryForageKey(id);
  await localforage.removeItem(forageKey);

  const removeLocal = () => {
    setKlinks((klinks) => {
      const temp = { ...klinks };
      delete temp[id];
      return temp;
    });
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
  // declare function for local update
  const updateKlink = (
    value: { name: string; description?: string; updatedAt: number },
  ) => {
    klink.name = value.name;
    klink.description = value.description;
    klink.updatedAt = value.updatedAt;
    setKlinks(klink.id, { ...klink });
  };

  const shared = isSharedEditable(klink);
  // handle update for shared klinks
  if (shared) {
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
  // update local copy with keys
  klink.readKey = response.readKey;
  klink.writeKey = response.writeKey;
  klink.updatedAt = relativeTime.unixFromResponse(response.updatedAt);
  setKlinks(klink.id, { ...klink });
}

function useKlinks(): Accessor<Klink[]> {
  return createMemo(() => Object.values(klinks));
}

export {
  copyExistingKlink,
  createNewKlink,
  deleteKlink,
  editKlink,
  shareKlink,
  useKlinks,
};
