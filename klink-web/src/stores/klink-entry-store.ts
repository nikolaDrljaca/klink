import { makePersisted } from "@solid-primitives/storage";
import localforage from "localforage";
import { createStore } from "solid-js/store";
import {
  isShared,
  isSharedEditable,
  klinkEntryForageKey,
} from "~/lib/klink-utils";
import makeKlinkApi from "~/lib/make-klink-api";
import { Klink, KlinkEntry } from "~/types/domain";
import { useSelectedKlink } from "./klink-store";
import { createEffect, createMemo, onCleanup } from "solid-js";
import makeAsync from "~/lib/make-async";

function buildSsePath(data: { id: string; readKey: string }): string {
  const API_PATH = import.meta.env.VITE_API_PATH;
  return `${API_PATH}/klink/${data.id}/events?readKey=${data.readKey}`;
}

function changesEventSource(
  klink: Klink,
  onMessage: (raw: any) => void,
): EventSource | void {
  const editable = isShared(klink);
  if (!editable) {
    return undefined;
  }
  const source = new EventSource(buildSsePath(klink));
  source.onmessage = (event) => onMessage(event.data);
  return source;
}

function createKlinkEntryStore(klink: Klink) {
  const id = klink.id;
  const store = createStore<Array<KlinkEntry>>([]);
  const [entries, setEntries] = makePersisted(
    store,
    {
      name: klinkEntryForageKey(id),
      storage: localforage,
    },
  );

  const editable = isSharedEditable(klink);
  const shared = isShared(klink);

  const api = makeKlinkApi();

  createEffect(() => {
    const eventSource = changesEventSource(
      klink,
      (raw) => {
        const entries: { value: string; createdAt: any }[] = JSON.parse(raw);
        setEntries(entries.map((it) => ({ value: it.value })));
      },
    );
    onCleanup(() => {
      if (eventSource) {
        console.log("closing event source");
        eventSource.close();
      }
    });
  });
  const cleanup = () => {
    // if (eventSource) {
    //   eventSource.close();
    // }
  };

  const addEntry = async (url: string) => {
    const entry: KlinkEntry = { value: url };
    if (editable) {
      await api.createKlinkEntry({
        klinkId: klink.id,
        readKey: klink.readKey,
        writeKey: klink.writeKey,
        klinkEntry: [entry],
      });
    } else {
      setEntries((val: KlinkEntry[]) => {
        const exists = val.find((it) => it.value === url);
        if (exists) {
          return;
        }
        return [...val, entry];
      });
    }
  };

  const removeEntry = async (url: string) => {
    if (editable) {
      await api.deleteKlinkEntries({
        klinkId: klink.id,
        readKey: klink.readKey,
        writeKey: klink.writeKey,
        klinkEntry: [{ value: url }],
      });
    } else {
      setEntries((val) => val.filter((it) => it.value !== url));
    }
  };

  const fetchEntries = async () => {
    if (!shared) {
      return;
    }
    const [err, data] = await makeAsync(api.getKlink({
      klinkId: klink.id,
      readKey: klink.readKey,
      writeKey: klink.writeKey,
    }));
    if (!data) {
      return;
    }
    setEntries(data.entries);
  };

  return {
    klink,
    entries,
    addEntry,
    removeEntry,
    fetchEntries,
    cleanup,
  };
}

export default function useKlinkEntries() {
  const klink = useSelectedKlink();
  return createMemo(() => createKlinkEntryStore(klink()));
}
