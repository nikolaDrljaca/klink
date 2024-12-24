import { Component, createSignal, JSX } from "solid-js";
import { Portal } from "solid-js/web";

type CreateKlinkModalProps = {
  children: (open: () => void) => JSX.Element,
  onSubmit: (data: { name: string, description?: string }) => void
}

const CreateKlinkModal: Component<CreateKlinkModalProps> = (props) => {
  // form state
  const [name, setName] = createSignal("");
  const [desc, setDesc] = createSignal("");
  const nameNotEmpty = () => name().length < 3;

  // modal handling
  let dialogRef: HTMLDialogElement;
  const show = () => {
    dialogRef.showModal();
  }

  const onSubmit = (event: Event) => {
    event.preventDefault();
    event.stopPropagation();

    props.onSubmit({
      name: name(),
      description: desc()
    });
    dialogRef.close();
    // clear form
    setName("");
    setDesc("");
  }

  return (
    <>
      {props.children(show)}
      <Portal>
        <dialog ref={dialogRef} class="modal">
          <div class="modal-box">
            {/* Klink Form */}
            <form class="flex flex-col space-y-4" onSubmit={onSubmit}>
              <p class="text-lg">Create Klink</p>
              <p class="font-light text-sm text-zinc-400">Create a new collection. You can share it once it's created.</p>
              <label class="input input-bordered flex items-center gap-2">
                <input
                  type="text"
                  class="grow"
                  value={name()}
                  onInput={(event) => setName(event.currentTarget.value)}
                  placeholder="Enter Here"
                />
              </label>
              <div class="divider"></div>
              <p class="font-light text-sm text-zinc-400">Optionally you can also provide a description.</p>
              <textarea
                class="textarea textarea-bordered"
                value={desc()}
                onInput={(event) => setDesc(event.target.value)}
                placeholder="Description">
              </textarea>
              <button
                disabled={nameNotEmpty()}
                class="btn btn-primary btn-sm">Create</button>
            </form>
          </div>
          <form method="dialog" class="modal-backdrop">
            <button>close</button>
          </form>
        </dialog>
      </Portal>
    </>
  );
}

export default CreateKlinkModal;
