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
      <div class="card text-base-content w-full">
        <div class="card-body items-center text-center">
          <h2 class="card-title text-error py-2">
            <CircleX size={40} />
          </h2>
          <p class="font-medium text-xl">Something went wrong.</p>
          <p>We could not find that collection.</p>
          <p>Check that you have the correct access keys.</p>
          <div class="flex flex-col xl:flex-row pt-4 space-y-4 xl:space-y-0">
            <KlinkKeyField key={store.readKey} title={"Read Key"} />
            <div class="divider divider-horizontal"></div>
            <KlinkKeyField key={store.writeKey} title={"Write Key"} />
          </div>
          <button class="btn btn-sm btn-primary mt-4 w-1/2" onClick={store.refetch}>Retry</button>
        </div>
      </div>
    );
  };

  return (
    <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">
      <div class="flex w-full items-center px-4 pt-4 pb-2">
        <p class="text-2xl"># Import Klink</p>
      </div>

      <div class="flex flex-col gap-x-4 px-4 pt-8 items-center h-full w-full">
        <ErrorBoundary fallback={<Error />}>
          <Suspense fallback={<Loading />}>
            {/* Klink Card */}
            <div class="card bg-base-300 text-base-content w-96">
              <div class="card-body items-center text-center">
                <Show when={store.data()}>
                  <h2 class="card-title">{store.data().name}</h2>
                  <div class="flex flex-row font-light text-sm space-x-2">
                    <span>Updated {store.updatedAt()}</span>
                    <span>&#8226;</span>
                    <span>{store.data().entries.length} Entries</span>
                  </div>
                </Show>
              </div>
            </div>

            {/* Action Row */}
            <p class="pt-8">Do you want to import this Klink?</p>
            <div class="card-actions justify-end pt-3">
              <button class="btn btn-sm btn-primary" onClick={onAccept}>Accept</button>
              <button class="btn btn-sm btn-ghost" onClick={onDeny}>Deny</button>
            </div>
          </Suspense>
        </ErrorBoundary>
      </div>
    </div>
  );
}

export default KlinkImport;
