import { makePersisted } from "@solid-primitives/storage";
import localforage from "localforage";
import { createStore } from "solid-js/store";
import { klinkEntryForageKey } from "~/lib/klink-utils";
import makeKlinkApi from "~/lib/make-klink-api";
import { KlinkEntry, KlinkModel } from "~/types/domain";
import { useSelectedKlink } from "./klink-store";
import { createEffect, createMemo, onCleanup } from "solid-js";
import makeAsync from "~/lib/make-async";

function buildSsePath(data: { id: string; readKey: string }): string {
  const API_PATH = import.meta.env.VITE_API_PATH;
  return `${API_PATH}/klink/${data.id}/events?readKey=${data.readKey}`;
}

function changesEventSource(
  klink: KlinkModel,
  onMessage: (raw: any) => void,
): EventSource | void {
  if (!klink.isShared) {
    return undefined;
  }
  const source = new EventSource(buildSsePath(klink));
  source.onmessage = (event) => onMessage(event.data);
  return source;
}

function createKlinkEntryStore(klink: KlinkModel) {
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
        eventSource.close();
      }
    });
  });

  const addEntry = async (url: string) => {
    const entry: KlinkEntry = { value: url };
    if (klink.isEditable) {
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
    if (klink.isEditable) {
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
    if (!klink.isShared) {
      return;
    }
    const [err, data] = await makeAsync(() =>
      api.getKlink({
        klinkId: klink.id,
        readKey: klink.readKey,
        writeKey: klink.writeKey,
      })
    );
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
  };
}

export default function useKlinkEntries() {
  const klink = useSelectedKlink();
  return createMemo(() => createKlinkEntryStore(klink()));
}
