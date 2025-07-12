import { makePersisted } from "@solid-primitives/storage";
import localforage from "localforage";
import { createStore } from "solid-js/store";
import { isSharedEditable, klinkEntryForageKey } from "~/lib/klink-utils";
import makeKlinkApi from "~/lib/make-klink-api";
import { Klink, KlinkEntry } from "~/types/domain";
import { useKlink, useSelectedKlink } from "./klink-store";
import { createMemo, untrack } from "solid-js";
import useKlinkIdParam from "~/hooks/use-klinkid-params";

type KlinkEntryStore = Array<KlinkEntry>;

function buildSsePath(data: { id: string; readKey: string }): string {
  const API_PATH = import.meta.env.VITE_API_PATH;
  return `${API_PATH}/klink/${data.id}/events?readKey=${data.readKey}`;
}

function changesEventSource(klink: Klink): EventSource | void {
  const editable = isSharedEditable(klink);
  if (!editable) {
    return undefined;
  }
  return new EventSource(buildSsePath(klink));
}

function createKlinkEntryStore(klink: Klink) {
  const id = klink.id;
  const store = createStore<KlinkEntryStore>([]);
  const [entries, setEntries] = makePersisted(
    store,
    {
      name: klinkEntryForageKey(id),
      storage: localforage,
    },
  );

  const editable = isSharedEditable(klink);

  const api = makeKlinkApi();

  const eventSource = changesEventSource(klink);
  if (eventSource) {
    eventSource.onmessage = (event) => {
      const entries: KlinkEntry[] = JSON.parse(event.data);
      setEntries(entries);
    };
  }
  const cleanup = () => {
    if (eventSource) {
      eventSource.close();
    }
  };

  // add entry
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
        return [entry, ...val];
      });
    }
  };
  // remove entry
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

  return {
    klink,
    entries,
    addEntry,
    removeEntry,
    cleanup,
  };
}

export default function useKlinkEntries() {
  const klink = useSelectedKlink();
  return createMemo(() => createKlinkEntryStore(klink()));
}
