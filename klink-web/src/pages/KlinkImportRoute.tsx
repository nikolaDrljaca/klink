import { useSearchParams } from "@solidjs/router";
import { CircleX } from "lucide-solid";
import { Component, createEffect, createResource, ErrorBoundary, Suspense } from "solid-js";
import KlinkCollection from "~/components/KlinkCollection";
import KlinkKeyField from "~/components/KlinkKeyField";
import KlinkSidebar from "~/components/KlinkSidebar";
import { KlinkApi } from "~/generated";
import useKlinkIdParam from "~/lib/useKlinkIdParam";

function useKlinkKeyParams(): { readKey: string | null, writeKey: string | null } {
  const [searchParams, _] = useSearchParams();

  const extract = (value: string | string[]): string | null => {
    if (Array.isArray(value)) {
      return null;
    }
    if (value === "") {
      return null;
    }
    return value as string;
  }

  return {
    readKey: extract(searchParams.read_key),
    writeKey: extract(searchParams.write_key)
  }
}

const KlinkImportRoute: Component = () => {
  // make request with createResource to have loading/error boundry
  // if success show component to accept Klink -> on yes put to local storage and redirect
  // error -> show error (including error responses due to wrong keys)
  // not found page if klinkId is not valid UUID

  // TODO: Move to a store - separate file
  const { readKey, writeKey } = useKlinkKeyParams();
  const klinkId = useKlinkIdParam();
  const api = new KlinkApi();
  const request = async () => {
    const curr = klinkId();
    if (!curr) {
      return Promise.reject();
    }
    return api.getKlink({
      klinkId: curr
    });
  }
  const [data] = createResource(request);

  // TODO: Move to a content component
  const Loading: Component = () => <div class="loading loading-spinner loading-lg"></div>;

  // TODO: Move to a content component
  const Error: Component = () => {
    return (
      <div class="card text-neutral-content w-96">
        <div class="card-body items-center text-center">
          <h2 class="card-title text-error py-2">
            <CircleX size={40} />
          </h2>
          <p class="font-medium text-xl">Something went wrong.</p>
          <p>We could not find that collection.</p>
          <p>Check that you have the correct access keys.</p>
          <div class="flex flex-row py-2">
            <KlinkKeyField key={readKey} title={"Read Key"} />
            <div class="divider divider-horizontal"></div>
            <KlinkKeyField key={writeKey} title={"Write Key"} />
          </div>
        </div>
      </div>
    );
  };

  // TODO: Keep as main content inside Suspend
  const Data: Component = () => {
    return (
      <div class="card bg-neutral text-neutral-content w-96">
        <div class="card-body items-center text-center">
          <h2 class="card-title">Collection Name</h2>
          <p class="py-2">Do you want to import this Klink?</p>
          <div class="card-actions justify-end">
            <button class="btn btn-sm btn-primary">Accept</button>
            <button class="btn btn-sm btn-ghost">Deny</button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div class="flex flex-row h-screen">
      {/* Sidebar */}
      <div class="w-1/6 h-full border-zinc-900 border-r-2">
        <KlinkSidebar />
      </div>

      {/* KlinkCollection */}
      <div class="w-2/6 h-full border-zinc-900 border-r-2">
        <KlinkCollection />
      </div>

      {/* Import Details */}
      <div class="w-2/6 h-full border-zinc-900 border-r-2">
        {/* TODO: Extract to content component */}
        <div class="flex flex-col w-full h-full grow overflow-y-scroll scrollbar-hidden">
          <div class="flex w-full justify-between items-center px-4 pt-4 pb-2">
            <p class="text-2xl"># Import Klink</p>
          </div>

          {/* Button Row */}
          <div class="flex flex-row gap-x-4 px-4 pt-2 pb-4 items-center justify-center h-full w-full">
            <ErrorBoundary fallback={<Error />}>
              <Suspense fallback={<Loading />}>
                <p>{data.latest.name}</p>
              </Suspense>
            </ErrorBoundary>
          </div>
        </div>

      </div>
    </div>
  );
}

export default KlinkImportRoute;
