import { A } from "@solidjs/router";
import { Image } from "@unpic/solid";
import { Component } from "solid-js";
import logo from "/images/logo.png";

const NotFoundRoute: Component = () => {
  return (
    <div class="py-6 sm:py-8 lg:py-12">
      <div class="mx-auto max-w-screen-2xl px-4 md:px-8">
        <div class="flex flex-col items-center">
          <a href="/" class="mb-8 inline-flex items-center gap-2.5 text-2xl font-bold  md:text-3xl" aria-label="logo">
            <Image src={logo} width={32} height={32} />
            Klink
          </a>

          <p class="mb-4 text-sm font-semibold uppercase text-primary md:text-base">That’s a 404</p>
          <h1 class="mb-2 text-center text-2xl font-bold md:text-3xl">Page not found</h1>

          <p class="mb-12 max-w-screen-md text-center text-gray-500 md:text-lg">The page you’re looking for doesn’t exist.</p>

          <A href="/c" class="btn btn-lg btn-primary">Go home</A>
        </div>
      </div>
    </div>
  );
}

export default NotFoundRoute;
