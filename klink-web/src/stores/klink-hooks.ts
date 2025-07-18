import { Accessor, createMemo } from "solid-js";
import { KlinkModel, klinkModel } from "~/types/domain";
import { KlinkService as service } from "./klink-store";
import useKlinkIdParam from "~/hooks/use-klinkid-params";

function useKlinks(): Accessor<{ key: string; model: Accessor<KlinkModel> }[]> {
  const klinks = service.getKlinkStore();
  return createMemo(() => {
    return Object.keys(klinks).map((it) => {
      const get = () => klinkModel(klinks[it]);
      return { key: it, model: get };
    });
  });
}

function useKlink(id: string): Accessor<KlinkModel> {
  const klinks = service.getKlinkStore();
  if (!klinks[id]) {
    throw new Error("Attempting to access non-existing collection.");
  }
  return createMemo(() => klinkModel(klinks[id]));
}

function useSelectedKlink(): Accessor<KlinkModel> {
  return createMemo(() => {
    const klinks = service.getKlinkStore();
    const klinkId = useKlinkIdParam();
    const klink = klinks[klinkId()];
    if (!klink) {
      return null;
    }
    return klinkModel(klink);
  });
}

export { useKlink, useKlinks, useSelectedKlink };
