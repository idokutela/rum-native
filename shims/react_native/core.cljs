(ns react-native.core
  "Implements the basic native components, and provides a few functions
  needed to use rum with react-native. Heavily inspired by the work in
  re-natal.

  All the react components in ReactNative 0.55 have been adapted to
  rum.  CamelCaseNames become spit-case, with IOS and Android becoming
  lowercase Thus DrawerLayoutAndroid becomes `drawer-layout-android`,
  and DatePickerIOS becomes `date-picker-ios`. The props for these rum
  components are converted from clojurescript, with keywords becoming
  camelCase. So one may, for example, have a fragment of text that
  does something on click with

  [text {:on-click dosomething} \"Hello, world!\"].

  The only exception to the above naming scheme is TabBarIOS.Item, which
  becomes `tab-bar-ios-item`.

  In addition, the utility `react->rum` turns react components into rum
  components.

  The function `style-sheet` makes react style sheets, that can be referenced
  by components.

  Basic usage:
    - define your UI with in rum, wiht a top level app component,
    - in your main file, define an init function that calls
      `mount-and-register`."
  (:require
   [clojure.string]
   [rum.core :as rum]
   ["react" :as React]
   ["react-native" :as ReactNative]
   ["create-react-class" :as create-class]))


;; Rum expects React to be global. We need to make sure of this:
(if (nil? (.-React js/window))
  (set! (.-React js/window) React))


;;; Utilities
(defn spit->camel
  "Takes a spit-case-string and makes it camel-case."
  [s]
  (let [[first & rest] (clojure.string/split s "-")]
    (apply str
           first
           (map clojure.string/capitalize rest))))

(defn clj->rejs
  "Exactly like `clj->js`, except that spit-case keywords are converted
  to CamelCase strings."
  [x]
  (cond
    (string? x) x
    (keyword? x) (spit->camel (name x))
    (map? x) (apply
              js-obj (transduce
                        (map #(clj->rejs %1 clj->rejs %2))
                        into
                        []
                        x))
    (coll? x) (apply array (map clj->rejs x))
    :else x))


;;; Basic react-native interop

(defn create-element
  "Creates a React element from the given `component`, `props` and `children`.

  The props are expected to be clojurescript maps, which are converted to
  js objects, with keywords being made camelcase."
  [component props & children]
  (apply (.-createElement React) component (clj->rejs props) children))

(defn react->rum
  [c]
  (partial create-element c))

(def app-registry
  (.-AppRegistry ReactNative))

;; React Native Components
(def activity-indicator (react->rum (.-ActivityIndicator ReactNative)))
(def button (react->rum (.-Button ReactNative)))
(def date-picker-ios (react->rum (.-DatePickerIOS ReactNative)))
(def drawer-layout-android (react->rum (.-DrawerLayoutAndroid ReactNative)))
(def flat-list (react->rum (.-FlatList ReactNative)))
(def image (react->rum (.-Image ReactNative)))
(def input-accessory-view (react->rum (.-InputAccessoryView ReactNative)))
(def keyboard-avoiding-view (react->rum (.-KeyboardAvoidingView ReactNative)))
(def list-view (react->rum (.-ListView ReactNative)))
(def masked-view-ios (react->rum (.-MaskedViewIOS ReactNative)))
(def modal (react->rum (.-Modal ReactNative)))
(def navigator-ios (react->rum (.-NavigatorIOS ReactNative)))
(def picker (react->rum (.-Picker ReactNative)))
(def picker-ios (react->rum (.-PickerIOS ReactNative)))
(def progress-bar-android (react->rum (.-ProgressBarAndroid ReactNative)))
(def progress-view-ios (react->rum (.-ProgressViewIOS ReactNative)))
(def refresh-control (react->rum (.-RefreshControl ReactNative)))
(def safe-area-view (react->rum (.-SafeAreaView ReactNative)))
(def scroll-view (react->rum (.-ScrollView ReactNative)))
(def section-list (react->rum (.-SectionList ReactNative)))
(def segmented-control-ios (react->rum (.-SegmentedControlIOS ReactNative)))
(def slider (react->rum (.-Slider ReactNative)))
(def snapshot-view-ios (react->rum (.-SnapshotViewIOS ReactNative)))
(def status-bar (react->rum (.-StatusBar ReactNative)))
(def switch (react->rum (.-Switch ReactNative)))
(def tab-bar-ios (react->rum (.-TabBarIOS ReactNative)))
(def tab-bar-ios-item (react->rum (.-Item (.-TabBarIOS ReactNative))))
(def text (react->rum (.-Text ReactNative)))
(def text-input (react->rum (.-TextInput ReactNative)))
(def toolbar-android (react->rum (.-ToolbarAndroid ReactNative)))
(def touchable-highlight (react->rum (.-TouchableHighlight ReactNative)))
(def touchable-native-feedback (react->rum (.-TouchableNativeFeedback ReactNative)))
(def touchable-opacity (react->rum (.-TouchableOpacity ReactNative)))
(def touchable-without-feedback (react->rum (.-TouchableWithoutFeedback ReactNative)))
(def view (react->rum (.-View ReactNative)))
(def view-pager-android (react->rum (.-ViewPagerAndroid ReactNative)))
(def virtualized-list (react->rum (.-VirtualizedList ReactNative)))
(def web-view (react->rum (.-WebView ReactNative)))

(defn style-sheet
  "Creates a react stylesheet from a cljs object."
  [styles]
  (-> ReactNative
      .-StyleSheet
      (.create (clj->rejs styles))))


;;; App mounting

;; We need some global state:
;; the root component of the app
(defonce ^:private root-component (atom nil))

;; the currently mounted element
(defonce ^:private mounted-element (atom nil))

;; A factory to create the root component
;; One should not need to reference this explicitly unless one is doing something crazy.
(defonce root-component-factory 
  ((.-createFactory React)
   (create-class
      #js {:getInitialState (fn []
                              (this-as this
                                (if-not @root-component
                                  (reset! root-component this)
                                  (throw (js/Error. "root component mounted more than once.")))))
           :render          (fn [] @mounted-element)})))

(defn mount
  "Mounts the given element into the app. If there is a currently mounted component,
   replaces it."
  [component]
  (reset! mounted-element component)
  (when @root-component
    (.forceUpdate @root-component)))

(defn register-app-component
  "Registers the given component-provider with the app registry.

   Unless you have know what you're doing, use mount-and-register."
  [name component-provider]
  (.registerComponent app-registry name component-provider))  
  
(defn mount-and-register
  "Mounts the given `component`, and registers the component with the
  givne `name` in the app registry."
  [name component]
  (mount component)
  (register-app-component name (fn [] root-component-factory)))
