import { useParams } from "@solidjs/router";

function useKlinkIdParam(): () => string | null {
  const params = useParams();
  return () => params.klinkId;
}

export default useKlinkIdParam;
