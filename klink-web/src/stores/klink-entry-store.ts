import { makePersisted } from "@solid-primitives/storage";
import localforage from "localforage";
import { createStore } from "solid-js/store";
import { klinkEntryForageKey } from "~/lib/klink-utils";
import makeKlinkApi from "~/lib/make-klink-api";
import { KlinkChangeEvent, KlinkEntry, KlinkModel } from "~/types/domain";
import { KlinkService as service } from "./klink-store";
import { createEffect, createMemo, onCleanup } from "solid-js";
import { useSelectedKlink } from "./klink-hooks";

function buildSsePath(data: { id: string; readKey: string }): string {
  const API_PATH = import.meta.env.VITE_API_PATH;
  return `${API_PATH}/klink/${data.id}/events?readKey=${data.readKey}`;
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

  // setup effect to connect to sse stream
  createEffect(() => {
    if (!klink.isShared) {
      return;
    }
    const source = new EventSource(buildSsePath(klink));
    source.onmessage = (raw) => {
      const event: KlinkChangeEvent = JSON.parse(raw.data);
      // if operation is deleted -> klink is local
      if (event.operation === "deleted") {
        service.makeLocal(klink.id);
        source.close();
        return;
      }
      setEntries(event.entries.map((it) => ({ value: it.value })));
    };

    onCleanup(() => {
      if (source) {
        source.close();
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

  return {
    klink,
    entries,
    addEntry,
    removeEntry,
  };
}

export default function useKlinkEntries() {
  const klink = useSelectedKlink();
  return createMemo(() => createKlinkEntryStore(klink()));
}
