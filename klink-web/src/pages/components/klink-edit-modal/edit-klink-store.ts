import { createSignal } from "solid-js";
import makeAsync from "~/lib/make-async";
import { useKlink } from "~/stores/klink-hooks";
import { KlinkService as service } from "~/stores/klink-store";

type EditKlinkModalEvent =
  | { type: "success" }
  | { type: "failure" };

export default function editKlinkStore(klinkId: string) {
  const klink = useKlink(klinkId);

  // form
  const [name, setName] = createSignal(klink().name);
  const [description, setDescription] = createSignal(klink().description ?? "");
  const [loading, setLoading] = createSignal(false);
  const isEditDisabled = () => {
    const nameInvalid = store.name().length < 3;
    return klink().isReadOnly || nameInvalid;
  };

  const store = {
    name: name,
    description: description,
    loading: loading,
    isEditDisabled: isEditDisabled,
  };

  const submit = async (): Promise<EditKlinkModalEvent> => {
    if (store.isEditDisabled() || loading()) {
      return;
    }
    setLoading(true);
    const [err, data] = await makeAsync(() =>
      service.editKlink({
        id: klinkId,
        name: name(),
        description: description(),
      })
    );
    if (err) {
      setLoading(false);
      return { type: "failure" };
    }
    setLoading(false);
    return { type: "success" };
  };

  return {
    state: store,
    setName,
    setDescription,
    submit,
    klink,
  };
}
