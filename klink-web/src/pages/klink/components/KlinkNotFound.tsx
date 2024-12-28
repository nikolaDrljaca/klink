import { FolderX } from "lucide-solid";
import { Component } from "solid-js";

const KlinkNotFound: Component = () => {
  return (
    <div class="flex flex-col space-y-4 w-full h-full items-center justify-center">
      <FolderX size={64} />
      <span class="font-medium text-xl">Klink Not Found</span>
    </div>
  );
}

export default KlinkNotFound;
