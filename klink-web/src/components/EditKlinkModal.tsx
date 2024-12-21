import { Component, Show } from "solid-js";
import editKlinkStore from "~/lib/editModalStore";

type EditKlinkModalProps = {
  klinkId: string,
  onClose: () => void,
}

const EditKlinkModal: Component<EditKlinkModalProps> = (props) => {
  const store = editKlinkStore(props.klinkId);

  const onSubmit = async (e: Event) => {
    e.preventDefault();
    e.stopPropagation();
    const result = await store.submit();
    if (result.type === 'success') {
      props.onClose();
    }
  }

  return (
    <form class="flex flex-col space-y-4" onSubmit={onSubmit}>
      <p class="text-lg">Edit</p>
      <p class="font-light text-sm text-zinc-400">Update Klink information. Save when ready.</p>

      <label class="input input-bordered flex items-center gap-2">
        <input
          type="text"
          class="grow"
          value={store.state.name}
          onInput={(event) => store.setName(event.currentTarget.value)}
          placeholder="Enter Name Here"
        />
      </label>

      <textarea
        class="textarea textarea-bordered"
        value={store.state.description}
        onInput={(event) => store.setDescription(event.target.value)}
        placeholder="Description">
      </textarea>

      <button
        disabled={store.state.isEditDisabled}
        class="btn btn-primary btn-sm">
        <Show when={store.state.loading}>
          <div class="loading loading-spinner"></div>
        </Show>
        Save
      </button>

    </form>
  );
}

export default EditKlinkModal;
