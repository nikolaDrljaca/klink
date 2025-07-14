import { createSignal, untrack } from "solid-js";
import useKlinkIdParam from "~/hooks/use-klinkid-params";
import makeAsync from "~/lib/make-async";
import { useKlinks } from "~/stores/klink-hooks";
import { KlinkService as service } from "~/stores/klink-store";

export default function collectionStore() {
  const pathKlinkId = useKlinkIdParam();
  const klinks = useKlinks();

  const [loading, setLoading] = createSignal(false);

  const reloadKlinkData = async () => {
    setLoading(true);
    const [err, value] = await makeAsync(() => service.syncKlinks());
    setLoading(false);
  };

  const copyKlink = (id: string) => {
    service.copyExistingKlink(id);
  };

  const createKlink = (data: {
    name: string;
    description?: string;
  }) => {
    service.createNewKlink(data);
  };

  return {
    klinks,
    reloadInProgress: loading,
    pathKlinkId,
    createKlink,
    copyKlink,
    reloadKlinkData,
  };
}
