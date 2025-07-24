import { Text } from "lucide-solid";
import { Component, createSignal } from "solid-js";
import CreateKlinkModal from "~/pages/components/klink-create-modal/CreateKlinkModal";

const CreateKlinkView: Component<
  { onSubmit: (value: { name: string; description?: string }) => void }
> = (props) => {
  const [form, setForm] = createSignal("");

  const isNameInvalid = () => form().length < 3;

  const onSubmit = (event: Event) => {
    event.stopPropagation();
    event.preventDefault();

    props.onSubmit({
      name: form(),
    });
    setForm("");
  };

  return (
    <form onSubmit={onSubmit}>
      <div class="flex flex-col w-full space-y-4 border-base-300 border-b-8 px-4 pt-2 pb-4">
        {/* Name Input */}
        <input
          type="text"
          placeholder="Klink Name"
          class="input bg-base-300 text-base-content w-full"
          required
          value={form()}
          onInput={(e) => {
            setForm(e.currentTarget.value);
          }}
        />
        {/* Button Row */}
        <div class="flex flex-row items-center justify-between pl-2">
          {/* Create Modal Button */}
          <div
            class="tooltip tooltip-right hover:cursor-pointer"
            data-tip="Add a description"
          >
            <CreateKlinkModal onSubmit={props.onSubmit}>
              {(open) => (
                <button
                  class="btn btn-sm btn-circle btn-ghost"
                  type="button"
                  onClick={open}
                >
                  <Text size={24} />
                </button>
              )}
            </CreateKlinkModal>
          </div>
          {/* Create Button */}
          <button
            class="btn btn-sm btn-primary"
            disabled={isNameInvalid()}
            type="submit"
          >
            Create
          </button>
        </div>
      </div>
    </form>
  );
};

export default CreateKlinkView;
