import { createSocialShare, FACEBOOK, MESSANGER, TELEGRAM, VIBER, WHATSAPP } from "@solid-primitives/share";
import { Image } from "@unpic/solid";
import { Component, For } from "solid-js";

const SocialsRow: Component<{
  shareTarget: {
    title: string,
    description: string,
    url: string
  }
}> = (props) => {
  const socials = [
    {
      name: WHATSAPP,
      icon: "WhatsApp",
      color: "25D366"
    },
    {
      name: VIBER,
      icon: "Viber",
      color: "7360F2"
    },
    {
      name: TELEGRAM,
      icon: "Telegram",
      color: "26A5E4"
    },
    {
      name: FACEBOOK,
      icon: "Facebook",
      color: "0866FF"
    },
    {
      name: MESSANGER,
      icon: "Messenger",
      color: "00B2FF"
    },
  ]

  const [share, _] = createSocialShare(() => props.shareTarget);

  const handleClick = (target: string) => {
    share(target);
  }

  return (
    <ul class="flex flex-nowrap items-center w-full">
      <For each={socials}>
        {item =>
          <button
            class="flex flex-col flex-1 items-center py-2 text-center rounded-md gap-y-2 hover:bg-base-300"
            onClick={() => handleClick(item.name)}>
            <Image src={`https://cdn.simpleicons.org/${item.icon}/${item.color}`} width={32} height={32} />
            {item.icon}
          </button>
        }
      </For>
    </ul>
  );
}

export default SocialsRow;
