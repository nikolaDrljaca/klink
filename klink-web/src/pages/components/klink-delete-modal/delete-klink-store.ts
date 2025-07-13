import { createSignal } from "solid-js";
import { deleteKlink, useKlink } from "~/stores/klink-store";
import makeAsync from "~/lib/make-async";

export default function deleteKlinkStore(klinkId: string) {
  const klink = useKlink(klinkId);

  const [loading, setLoading] = createSignal(false);
  const [shouldDeleteShared, setShouldDeleteShared] = createSignal(false);

  const handleDelete = async () => {
    if (loading()) {
      return;
    }
    setLoading(true);
    const [err, data] = await makeAsync(
      () => deleteKlink(klinkId, shouldDeleteShared()),
    );
    setLoading(false);
    return err;
  };

  return {
    loading,
    shouldDeleteShared,
    klink,

    handleDelete,

    setShouldDeleteShared(value: boolean) {
      setShouldDeleteShared(value);
    },
  };
}
