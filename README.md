# React Native, `rum` and `shadow-cljs`

A super simple way to develop react native apps with clojurescript, or
add clojurescript modules to existing react-native projects.

## Why?

`re-natal` is awesome, but I always have the feeling that one needs a
lot of magic going on. Also, it uses `lein`, and I want to use
`shadow-cljs`.

## If I’m starting a new react native project.

Create the project:

    react-native init <project-name>

Install [`shadow-cljs`](https://github.com/thheller/shadow-cljs) if
you haven’t already. With `yarn`, that goes as follows:

    yarn add --dev shadow-cljs

Clone the repo:

    git clone https://github.com/idokutela/rum-native.git
	
Make a folder to contain your own clojurescript source:

    mkdir cljs

Make a `shadow-cljs.edn` to suit your config. Be sure to include
`rum-native` and your source path in the source-paths.

A simple example (heavily annotated):

```clojure
;; shadow-cljs.edn

{:source-paths
 ["cljs" "rum-native"] ; Including rum-native makes sure rum works!

;; No need for exclusions, because shadow-cljs prefers local files
;; to deps
 :dependencies
 [[rum "0.11.2"]]

;; At the moment, the :npm-module target is the only one I can get to
;; work
 :builds
 {:app {:target :npm-module
        :output-dir "lib"}}}
```

Now, start coding! But see the guide below…

## If I already have a react native project

Do as above, except don’t create a project!

## Using `rum`
One can use rum exactly as in the browser. The only difference:
instead of using dom components, one uses the components defined in
`react-native.core`. These are simple wrappers of the React Native
0.55 components. Their names are precisely as in React Native, except
that CamelCase becomes spit-case, and IOS becomes ios. Props are just
clojure maps, with skewer-case keys replacing the React camelCase
keys.

A simple example should make this clear:

```clojure
(ns my-example.hello-world
  (:require [rum.core :refer [defc]]
            [react-native.core :as react-native]))
			
(defc hello-world-component
  []
  (react-native/text {:style {:font-size 30
                              :font-weight :bold}} 
	"Hello, world!"))
```

One gotcha: all the React native components _must_ be passed a props
map, even if this is empty. I’ll probably change this in a future
update.

If one has React Native components from external libraries, 
`react->rum` wraps them for use with `rum`:

```clojure
(ns my-example.wrapped-component
  (:require [react-native.core :refer [react->rum]]
            ["fancy-component" :as FancyComponent]))
  
(def fancy-component (react->rum FancyComponent))
```

`react-native` also wraps the `StyleSheet` helper. One uses it as
follows:

```clojure
(ns my-example.styled-hello-world
  (:require [rum.core :refer [defc]]
            [react-native.core :as react-native]))
			
(defn styles 
  (react-native/style-sheet 
    {:text {:font-size 30 :font-weight :bold}}))

(defc hello-world-component
  []
  (react-native/text {:style (.-text styles)}
	"Hello, world!"))
```

A current gotcha: one needs to refer to the styles with javascript
accessors!

Finally, if one wants to mount a `rum` component, one uses
`mount-and-register`:

```clojure
(ns my-example.core
  (:require [react-native.core :refer [mount-and-register]]
            [my-example.hello-world :refer [hello-world]])
			
(defn init
  []
  (mount-and-register
    "MyApp" (hello-world)))
```

The name "MyApp" should of course be replaced with your app’s name.

Finally, we need react native to know to use this. Simply edit
`index.js` to call init:

```js
import {init} from "./lib/my_example.core";

init();
```

There really is no magic: have a look at `react_native/core.cljs` to
convince yourself!

## Development workflow

I find it convenient to do the following:

 - in one terminal, run 
       
	    yarn shadow-cljs watch app
		
   This watches my clojurescript, and compiles it as it changes.
   
 - start a device emulator, or connect a device.
 - in another terminal, run
 
        react-native run-ios / react-native run-android
		
   and make sure to enable [hot
   reloading](https://facebook.github.io/react-native/blog/2016/03/24/introducing-hot-reloading.html). No
   need for figwheel!
   
## What doesn’t work
- I don't yet know how to get a working repl.
- Source maps are messed up. Unfortunately, there’s not much that will
  change here: react’s bundler ignores source maps when bundling js.
- This is deliberately not on clojars: it's completely experimental,
  and likely to change quite a bit.
  
## References

 - [`re-natal`](https://github.com/drapanjanas/re-natal): an excellent
   way to quickly get going with react native and
   clojurescript. `react-native/core` was very strongly inspired by
   their work.
 - [`shadow-cljs`](https://github.com/drapanjanas/re-natal): a lovely
   way to build clojurescript
 - [`rum`](https://github.com/tonsky/rum): a clean react cljs library.

## License

Public domain: you're free to do whatever you want with this. However,
I accept no liability for the use you put it to, nor make any claim
that it is fit for any purpose.

