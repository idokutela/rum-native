# React Native, Rum and `shadow-cljs`

I love `re-natal`, but there‘s a bit too much magic for my taste.
This presents a “magic-free” way to develop for React Native using
clojurescript, using [`rum`](`https://github.com/tonsky/rum`) for
interface building, and
[`shadow-cljs`](https://github.com/thheller/shadow-cljs) to compile
the ClojureScript.

## Quickstart

1. Clone `rum-native`
   
        git clone https://github.com/idokutela/rum-native.git

2. Create a react-native project:

        react-native init <project-name>
		
3. Copy the relevant content from rum-native over to the project:

        cd rum-native
		./copy-repo.sh ../<project-name>
		
4. Delete the repo

        cd ..
		rm -rf rum-native
		
   Be careful with `rm -rf`! Make sure you're deleting the correct
   folder.
   
4. Go to the project

        cd ../<project-name>

5. Install `shadow-cljs` if you don’t already have it.

        yarn add --dev shadow-cljs
		
   or
   
        npm i -D shadow-cljs
		
   If you don’t mind global installs, you can add shadow-cljs
   globally.
   
   If you’ve installed it locally, you may wish to add the following
   scripts to `package.json`:
   
   ```js
   // package.json
   { 
     /* ... */
	 "scripts": {
	    /* other scripts */
	    "shadow": "shadow-cljs",
		"server": "shadow-cljs server",
		"watch": "shadow-cljs watch app",
		"build": "shadow-cljs compile app"
	 } 
	 /* ... */
   }
   ```

5. Now there’s just a little cleanup before the first build. First, we
   don’t need `App.js`:
   
        rm App.js
		
6. Second, you probably want a different namespace than
   `my-app`. To change this:
   
   1. Rename the folder `cljs/my-app` to `cljs/<namespace>`,
   2. Edit `index.js` to import `init` from `<namespace>.core`.
   
7. Finally, we edit `cljs/<namespace>/core.cljs`:

   1. Correct the namespace,
   2. Change 
   
   ```clojure
   (mount-and-register "MyApp" (App state))
   ```
   
   to
   
   ```clojure
   (mount-and-register "<project-name>" (App state))
   ```
   
8. Now we're ready to develop!

   1. Start a device emulator,
   2. Build and watch the clojurescript:
   
        yarn watch
		
   3. Start react-native:
   
        react-native run-ios/run-android
		
	  The first load will probably take a looong time!
   4. Enable
      [hot-reloading](https://facebook.github.io/react-native/blog/2016/03/24/introducing-hot-reloading.html)
      on the device.
	  
9. Boom! Time to start developing!
   
   

