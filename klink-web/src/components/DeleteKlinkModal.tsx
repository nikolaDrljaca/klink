import { Component, createSignal, Show } from "solid-js"
import { Klink } from "~/lib/klinks/store"

type DeleteKlinkModalProps = {
  klink: Klink,
  onClose: () => void,
  onSubmit: (data: { klinkId: string, deleteRemote: boolean }) => void,
}

const DeleteKlinkModal: Component<DeleteKlinkModalProps> = (props) => {
  // form state
  const isKlinkShared = () => props.klink.readKey;
  const [shouldDeleteShared, setShouldDeleteShared] = createSignal(false);

  const onSubmit = (event: SubmitEvent) => {
    event.preventDefault();
    event.stopPropagation();
    // @ts-ignore
    const confirmed = event.submitter.value === 'yes';
    if (confirmed) {
      props.onSubmit({
        klinkId: props.klink.id,
        deleteRemote: shouldDeleteShared()
      });
    } else {
      props.onClose();
    }
  }

  return (
    <div>
      {/* Klink Form */}
      <form class="flex flex-col space-y-4" onSubmit={onSubmit}>
        <p class="text-lg">Are you sure?</p>
        <p class="font-light text-sm text-zinc-400">You are about to delete a klink.</p>

        <Show when={isKlinkShared()}>
          <div class="flex flex-col w-full">
            <div class="divider"></div>
            <div class="form-control">
              <label class="label cursor-pointer">
                <span class="label-text">Delete for everyone?</span>
                <input
                  type="checkbox"
                  checked={shouldDeleteShared()}
                  onInput={e => setShouldDeleteShared(e.target.checked)}
                  class="checkbox" />
              </label>
            </div>
            <p class="text-sm pl-1 text-zinc-400 font-light">This will delete the collection from the cloud!</p>
          </div>
        </Show>

        <div class="flex flex-row space-x-2 justify-end items-center w-full">
          <button class="btn btn-sm" value="no">No</button>
          <button class="btn btn-primary btn-sm" value="yes">Yes</button>
        </div>
      </form>
    </div>
  );
}

export default DeleteKlinkModal;
