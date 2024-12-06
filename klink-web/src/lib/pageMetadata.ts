
export async function getPageMetadata(url: string): Promise<{ title?: string, error?: string }> {
    try {
        const response = await fetch(url);
        if (!response.ok) {

        }
        const html = await response.text();

        const parser = new DOMParser();
        const doc = parser.parseFromString(html, "text/html");
        const title = doc.querySelector('title')?.textContent || undefined;

        return { title };
    } catch (error) {
        return { error: "Unkown error occured" };
    }
}
