import { createSignal } from "solid-js";
import { KlinkService as service } from "~/stores/klink-store";
import makeAsync from "~/lib/make-async";
import { useKlink } from "~/stores/klink-hooks";

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
      () => service.deleteKlink(klinkId, shouldDeleteShared()),
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
