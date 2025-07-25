import { useNavigate } from "@solidjs/router";
import { createEffect } from "solid-js";
import toast from "solid-toast";
import createPasteKeyEvent from "~/lib/create-paste-key-event";
import { readLastClipboardEntry } from "~/lib/read-clipboard";
import { useKlinkEntries } from "~/stores/klink-entry-store";
import { isUrl } from "~/types/domain";

export default function createEntriesStore() {
  const store = useKlinkEntries();
  const navigate = useNavigate();

  // setup paste keyboard handling
  const pasteKey = createPasteKeyEvent();
  createEffect(async () => {
    if (!pasteKey()) {
      return;
    }
    try {
      const entry = await readLastClipboardEntry();
      if (isUrl(entry)) {
        await store().addEntry(entry);
      }
    } catch (e) {
      toast.error("Failed to read clipboard!");
    }
  });

  const createEntry = async (value: string) => {
    await store().addEntry(value);
  };

  const handleDelete = async (value: string) => {
    await store().removeEntry(value);
  };

  const handleBack = () => {
    if (history.length <= 2) {
      navigate("/c", {
        replace: true,
      });
    } else {
      navigate(-1);
    }
  };

  return () => ({
    entries: store().entries,
    klink: store().klink,
    createEntry,
    handleDelete,
    handleBack,
  });
}
