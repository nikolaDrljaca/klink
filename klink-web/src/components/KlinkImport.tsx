import { CircleX } from "lucide-solid";
import { Component, ErrorBoundary, Show, Suspense } from "solid-js";
import KlinkKeyField from "./KlinkKeyField";
import importKlinkStore from "~/lib/importKlinkStore";
import { useNavigate } from "@solidjs/router";

const KlinkImport: Component = () => {
  const store = importKlinkStore();
  const navigate = useNavigate();

  const onAccept = () => {
    store.importKlink();
    const klinkId = store.data.latest.id;
    if (klinkId) {
      navigate(`/c/${klinkId}`);
    }
  }

  const onDeny = () => {
    // navigate out
    navigate("/c");
  }

  const Loading: Component = () => <div class="loading loading-spinner loading-lg"></div>;

  const Error: Component = () => {
    return (
      <div class="card text-neutral-content w-full">
        <div class="card-body items-center text-center">
          <h2 class="card-title text-error py-2">
            <CircleX size={40} />
          </h2>
          <p class="font-medium text-xl">Something went wrong.</p>
          <p>We could not find that collection.</p>
          <p>Check that you have the correct access keys.</p>
          <div class="flex flex-row pt-4">
            <KlinkKeyField key={store.readKey} title={"Read Key"} />
            <div class="divider divider-horizontal"></div>
            <KlinkKeyField key={store.writeKey} title={"Write Key"} />
          </div>
        </div>
      </div>
    );
  };

  return (
    <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">
      <div class="flex w-full justify-between items-center px-4 pt-4 pb-2">
        <p class="text-2xl"># Import Klink</p>
      </div>

      <div class="flex flex-row gap-x-4 px-4 pt-2 pb-4 items-center justify-center h-full w-full">
        <ErrorBoundary fallback={<Error />}>
          <Suspense fallback={<Loading />}>
            <div class="card bg-neutral text-neutral-content w-96">
              <div class="card-body items-center text-center">
                <Show when={store.data()}>
                  <h2 class="card-title">{store.data().name}</h2>
                </Show>
                <p class="py-2">Do you want to import this Klink?</p>
                <div class="card-actions justify-end">
                  <button class="btn btn-sm btn-primary" onClick={onAccept}>Accept</button>
                  <button class="btn btn-sm btn-ghost" onClick={onDeny}>Deny</button>
                </div>
              </div>
            </div>
          </Suspense>
        </ErrorBoundary>
      </div>
    </div>
  );
}

export default KlinkImport;
