import { A } from "@solidjs/router";
import { Component } from "solid-js";

const NotFoundRoute: Component = () => {
  return (
    <div class="py-6 sm:py-8 lg:py-12">
      <div class="mx-auto max-w-screen-2xl px-4 md:px-8">
        <div class="flex flex-col items-center">
          <a href="/" class="mb-8 inline-flex items-center gap-2.5 text-2xl font-bold text-white md:text-3xl" aria-label="logo">
            <svg width="95" height="94" viewBox="0 0 95 94" class="h-auto w-6 text-primary" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
              <path d="M96 0V47L48 94H0V47L48 0H96Z" />
            </svg>

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
