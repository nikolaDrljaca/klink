import { useNavigate } from "@solidjs/router";
import { Component, onCleanup, Show } from "solid-js"
import toast from "solid-toast";
import deleteKlinkStore from "~/lib/deleteKlinkStore";

type DeleteKlinkModalProps = {
  klinkId: string,
  onClose: () => void,
}

const DeleteKlinkModal: Component<DeleteKlinkModalProps> = (props) => {
  // form state
  const store = deleteKlinkStore(props.klinkId);
  const navigate = useNavigate();

  const onSubmit = async (event: SubmitEvent) => {
    event.preventDefault();
    event.stopPropagation();
    // @ts-ignore
    const confirmed = event.submitter.value === 'yes';
    if (confirmed) {
      await store.deleteKlink();
    } else {
      props.onClose();
    }
  }

  const unsub = store.listen(event => {
    switch (event.type) {
      case "success":
        toast('Klink deleted.');
        navigate('/c');
        break;
      case "failure":
        toast.error('Something went wrong.');
        break;
    }
  });

  onCleanup(() => unsub());

  return (
    <div>
      {/* Klink Form */}
      <form class="flex flex-col space-y-4" onSubmit={onSubmit}>
        <p class="text-lg">Are you sure?</p>
        <p class="font-light text-sm text-zinc-400">You are about to delete a klink.</p>

        <Show when={store.state.isShared}>
          <div class="flex flex-col w-full">
            <div class="divider"></div>
            <div class="form-control">
              <label class="label cursor-pointer">
                <span class="label-text">Delete for everyone?</span>
                <input
                  type="checkbox"
                  checked={store.state.shouldDeleteShared}
                  onInput={e => store.setShouldDeleteShared(e.target.checked)}
                  class="checkbox" />
              </label>
            </div>
            <p class="text-sm pl-1 text-zinc-400 font-light">This will delete the collection from the cloud!</p>
          </div>
        </Show>

        <div class="flex flex-row space-x-2 justify-end items-center w-full">
          <button class="btn btn-sm" value="no">No</button>
          <button class="btn btn-primary btn-sm" value="yes">
            <Show when={store.state.loading}>
              <span class="loading loading-spinner"></span>
            </Show>
            Yes
          </button>
        </div>
      </form>
    </div>
  );
}

export default DeleteKlinkModal;
