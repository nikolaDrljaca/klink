import { createSignal } from "solid-js";
import useKlinkIdParam from "~/hooks/use-klinkid-params";
import makeAsync from "~/lib/make-async";
import {
  copyExistingKlink,
  createNewKlink,
  syncKlinks,
  useKlinks,
} from "~/stores/klink-store";

export default function collectionStore() {
  const pathKlinkId = useKlinkIdParam();
  const klinks = useKlinks();

  const [loading, setLoading] = createSignal(false);

  const reloadKlinkData = async () => {
    setLoading(true);
    const [err, value] = await makeAsync(syncKlinks());
    setLoading(false);
  };

  const copyKlink = (id: string) => {
    copyExistingKlink(id);
  };

  const createKlink = (data: {
    name: string;
    description?: string;
  }) => {
    createNewKlink(data);
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
