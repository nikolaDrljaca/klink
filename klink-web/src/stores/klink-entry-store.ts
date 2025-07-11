import { makePersisted } from "@solid-primitives/storage";
import localforage from "localforage";
import { createStore } from "solid-js/store";
import { isSharedEditable, klinkEntryForageKey } from "~/lib/klink-utils";
import makeKlinkApi from "~/lib/make-klink-api";
import { KlinkEntry } from "~/types/domain";
import { useKlink } from "./klink-store";
import { untrack } from "solid-js";

type KlinkEntryStore = Array<KlinkEntry>;

function createKlinkEntryStore(id: string) {
  const store = createStore<KlinkEntryStore>([]);
  const [entries, setEntries] = makePersisted(
    store,
    {
      name: klinkEntryForageKey(id),
      storage: localforage,
    },
  );

  const klink = untrack(useKlink(id));
  const editable = isSharedEditable(klink);

  const api = makeKlinkApi();

  // TODO: finish out
  const eventSource = new EventSource("");
  eventSource.onmessage = (event) => {
    const entries: KlinkEntry[] = JSON.parse(event.data);
    setEntries(entries);
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
    // TODO: need endpoint here
    if (editable) {
    } else {
      setEntries((val) => val.filter((it) => it.value !== url));
    }
  };
}
