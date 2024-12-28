import { A } from "@solidjs/router";
import { HardHat } from "lucide-solid";
import { Component } from "solid-js";

const ComingSoonRoute: Component = () => {
  return (
    <div class="py-6 sm:py-8 lg:py-12">
      <div class="mx-auto max-w-screen-2xl px-4 md:px-8">
        <div class="flex flex-col items-center">
          <a href="/" class="mb-8 inline-flex items-center gap-2.5 text-2xl font-bold  md:text-3xl" aria-label="logo">
            <HardHat size={70} />
          </a>

          <h1 class="mb-2 text-center text-2xl font-bold md:text-3xl">Coming Soon</h1>

          <p class="mb-12 max-w-screen-md text-center text-gray-500 md:text-lg">This page is still in development.</p>

          <A href="/c" class="btn btn-lg btn-primary">Home</A>
        </div>
      </div>
    </div>
  );
}

export default ComingSoonRoute;
