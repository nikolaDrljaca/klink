import { createSignal, ParentProps, Show } from "solid-js";
import { Portal } from "solid-js/web";

function createModal() {
  const [isOpen, setIsOpen] = createSignal(false);

  let dialogRef: HTMLDialogElement;

  const open = () => {
    setIsOpen(true);
    setTimeout(() => dialogRef.showModal(), 0);
  }

  const onSubmit = () => {
    // wait for the animation to finish and then remove from DOM
    setTimeout(() => setIsOpen(false), 500);
  }
  const close = () => {
    dialogRef.close();
    setTimeout(() => setIsOpen(false), 500);
  }

  return {
    controller: { open, close },
    Modal(props: ParentProps) {
      return (
        <Show when={isOpen()}>
          <Portal>
            <dialog ref={dialogRef} class="modal">
              <div class="modal-box">
                {/* Modal Body */}
                {props.children}
              </div>
              <form method="dialog" class="modal-backdrop" onSubmit={onSubmit}>
                <button>close</button>
              </form>
            </dialog>
          </Portal>
        </Show>
      );
    }
  }
}

export default createModal;
