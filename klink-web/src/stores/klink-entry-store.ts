import { makePersisted } from "@solid-primitives/storage";
import localforage from "localforage";
import { createStore } from "solid-js/store";
import { klinkEntryForageKey } from "~/lib/klink-utils";
import makeKlinkApi from "~/lib/make-klink-api";
import { KlinkChangeEvent, KlinkEntry, KlinkModel } from "~/types/domain";
import { KlinkService as service } from "./klink-store";
import { Accessor, createMemo } from "solid-js";
import { useSelectedKlink } from "./klink-hooks";
import makeAsync from "~/lib/make-async";
import toast from "solid-toast";
import createKlinkEventStream from "~/lib/create-klink-event-stream";

function buildSsePath(data: { id: string; readKey: string }): string {
  const API_PATH = import.meta.env.VITE_APP_WS;
  return `${API_PATH}/events/klink/${data.id}?read_key=${data.readKey}`;
}

type KlinkEntriesStore = {
  klink: KlinkModel;
  entries: KlinkEntry[];
  addEntry: (url: string) => Promise<void>;
  removeEntry: (url: string) => Promise<void>;
};

function createKlinkEntryStore(klink: KlinkModel): KlinkEntriesStore {
  const id = klink.id;
  const store = createStore<Array<KlinkEntry>>([]);
  const [entries, setEntries] = makePersisted(
    store,
    {
      name: klinkEntryForageKey(id),
      storage: localforage,
    },
  );

  const api = makeKlinkApi();

  // setup effect to connect to event stream
  createKlinkEventStream({
    url: buildSsePath(klink),
    onMessage: (raw, close) => {
      const event: KlinkChangeEvent = JSON.parse(raw.data);

      if (event.operation === "deleted") {
        service.makeLocal(klink.id);
        close();
        return;
      }

      setEntries(
        event.entries.map((it) => ({
          value: it.value,
          title: it.title,
          description: it.description,
        })),
      );
    },
    maxRetries: 5,
    retryDelayMs: 4000,
  });

  const createEntry = (entry: KlinkEntry) => {
    const exists = entries.find((it) => it.value === entry.value);
    if (exists) {
      return;
    }
    setEntries(entries.length, entry);
  };

  const deleteEntry = (entry: KlinkEntry) => {
    setEntries((val) => {
      return val.filter((it) => it.value !== entry.value);
    });
  };

  const addEntry = async (url: string) => {
    const entry: KlinkEntry = { value: url };
    createEntry(entry);
    if (klink.isEditable) {
      const [err, data] = await makeAsync(() =>
        api.createKlinkEntry({
          klinkId: klink.id,
          readKey: klink.readKey,
          writeKey: klink.writeKey,
          klinkEntry: [entry],
        })
      );
      if (err) {
        deleteEntry(entry);
        toast.error("Couldn't create entry. Try again later.");
      }
    }
  };

  const removeEntry = async (url: string) => {
    deleteEntry({ value: url });
    if (klink.isEditable) {
      const [err, data] = await makeAsync(() =>
        api.deleteKlinkEntries({
          klinkId: klink.id,
          readKey: klink.readKey,
          writeKey: klink.writeKey,
          klinkEntry: [{ value: url }],
        })
      );
      if (err) {
        createEntry({ value: url });
        toast.error("Couldn't delete entry. Try again later.");
      }
    }
  };

  return {
    klink,
    entries,
    addEntry,
    removeEntry,
  };
}

export function useKlinkEntries(): Accessor<KlinkEntriesStore> {
  const klink = useSelectedKlink();
  return createMemo(() => createKlinkEntryStore(klink()));
}
