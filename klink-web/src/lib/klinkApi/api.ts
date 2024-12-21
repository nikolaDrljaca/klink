import { Configuration, KlinkApi } from "~/generated";

const API_PATH = import.meta.env.VITE_API_PATH;

const api = new KlinkApi(
    new Configuration({
        basePath: API_PATH
    })
);

export default function klinkApi(): KlinkApi {
    return api;
}
