 (function f() {
	 if (!(window.DevRevSDK && window.DevRevSDK.mobilesdk && window.DevRevSDK.mobilesdk.monitor)) {
		 function applySDK() {
			 window.DevRevSDK = window.DevRevSDK || {};

			 let sdk = window.DevRevSDK.mobilesdk = window.DevRevSDK.mobilesdk || { monitor: {} };
			 if (window.DevRevSDK.mobilesdk.monitor === undefined) {
				 window.DevRevSDK.mobilesdk.monitor = {};
			 };
			 let monitor = window.DevRevSDK.mobilesdk.monitor = window.DevRevSDK.mobilesdk.monitor || sdk.monitor;
			 let selectors = window.DevRevSDK.mobilesdk.PIISelectors = window.DevRevSDK.mobilesdk.PIISelectors || undefined;

			 if (MutationObserver) {
				 let mo = new MutationObserver((mutationsList) => {
					 window.DevRevSDK.mobilesdk.domChanged = true;

					 mutationsList.forEach(mutation => {
						 mutation.addedNodes.forEach(node => {
							 if (node.shadowRoot) {
								 observeShadowDom(node.shadowRoot);
							 }
						 });
					 });
				 });

				 const observedShadowRoots = new WeakSet();

				 function observeShadowDom(shadowRoot) {
					 if (observedShadowRoots.has(shadowRoot)) return;

					 observedShadowRoots.add(shadowRoot);
					 mo.observe(shadowRoot, { subtree: true, childList: true, attributes: true });

					 shadowRoot.querySelectorAll('*').forEach(el => {
						 if (el.shadowRoot) {
							 observeShadowDom(el.shadowRoot);
						 }
					 });
				 };

				 function initializeObservers() {
					 document.querySelectorAll('*').forEach(el => {
						 if (el.shadowRoot) {
							 observeShadowDom(el.shadowRoot);
						 }
					 });

					 mo.observe(document.documentElement, { childList: true, subtree: true });

					 document.querySelectorAll('*').forEach(el => {
						 const tagName = el.tagName.toLowerCase();
						 if (tagName.startsWith('ion-')) {
							 customElements.whenDefined(tagName).then(() => {
								 document.querySelectorAll(tagName).forEach(el => {
									 if (el.shadowRoot) {
										 observeShadowDom(el.shadowRoot);
									 }
								 });
							 });
						 }
					 });
				 };

				 if (document.readyState === 'loading') {
					 document.addEventListener('DOMContentLoaded', initializeObservers);
				 } else {
					 initializeObservers();
				 }

				 const originalAttachShadow = Element.prototype.attachShadow;
				 Element.prototype.attachShadow = function (init) {
					 const shadowRoot = originalAttachShadow.call(this, init);
					 observeShadowDom(shadowRoot);
					 return shadowRoot;
				 };

				 document.addEventListener('ionViewDidEnter', () => {
					 document.querySelectorAll('*').forEach(el => {
						 if (el.shadowRoot) {
							 observeShadowDom(el.shadowRoot);
						 }
					 });
				 });
			 };

			 !function () {
				 if (!monitor.isScrollHandlerInstalled) {
					 monitor.isScrollHandlerInstalled = true;
					 monitor.isScrolling = false;

					 window.addEventListener('scroll', function () {
						 monitor.isScrolling = true;
						 if (monitor.scrollTimeout) {
							 clearTimeout(monitor.scrollTimeout);
						 }
						 monitor.scrollTimeout = setTimeout(function () {
							 monitor.isScrolling = false;
						 }, 750);
					 });
				 };
			 }();

			 !function () {
				 function renewTimeout(data) {
					 data.isZooming = true;
					 if (data.zoomTimeout) {
						 clearTimeout(data.zoomTimeout);
					 };
					 data.zoomTimeout = setTimeout(function () {
						 data.isZooming = false;
					 }, 750);
				 };

				 if (!monitor.isTapActionHandlerInstalled) {
					 monitor.isTapActionHandlerInstalled = true;
					 monitor.isZooming = false;

					 document.body.addEventListener("touchend", function () {
						 var now = new Date().getTime();
						 var lastTouch = monitor.ueLastTouchTimestamp || now + 1;
						 var delta = now - lastTouch;
						 monitor.ueLastTouchTimestamp = now;
						 if (delta > 0 && delta < 50) {
							 renewTimeout(monitor);
						 };
					 });
				 };

				 if (!monitor.isFocusHandlerInstalled) {
					 monitor.isFocusHandlerInstalled = true;
					 monitor.isZooming = false;
					 document.querySelectorAll('input').forEach(function (e) {
						 e.addEventListener('focus', function () { renewTimeout(monitor); }, false);
					 });
				 };
			 }();

			 sdk.getBlockedLocations = function () {
                 function serializeRects(rectangles) {
                     const isIOS = (function () {
                         var isIOSDevice = /iPad|iPhone|iPod/.test(navigator.userAgent);
                         var isAppleDevice = navigator.userAgent.includes('Macintosh');
                         var isTouchScreen = navigator.maxTouchPoints >= 1;

                         return isIOSDevice || (isAppleDevice && isTouchScreen);
                     })();

                     const scrollX = window.scrollX || window.pageXOffset;
                     const scrollY = window.scrollY || window.pageYOffset;

                     return rectangles.map(function (e) {
                         const top = e.top + scrollY;
                         const left = e.left + scrollX;

                         if (isIOS) {
                             // iOS format: flat array origin and size to make it CGRect compatible.
                             return [
                                 [left, top],
                                 [e.width, e.height]
                             ];
                         } else {
                             // Other platforms: object format
                             return {
                                 origin: {
                                     x: e.left,
                                     y: e.top
                                 },
                                 size: {
                                     width: e.width,
                                     height: e.height
                                 }
                             };
                         }
                     });
                 };

				 function isElementHidden(element) {

					 var style = window.getComputedStyle(element);
					 return style.getPropertyValue('display') === 'none' ||
						 style.getPropertyValue('visibility') === 'hidden' ||
						 style.getPropertyValue('opacity') === "0" ||
						 element.offsetParent === null || (!element.offsetWidth || !element.offsetHeight);
				 };
				 function isInViewport(element) {
					 const shadowWindow = {
						 top    : 0,
						 left   : 0,
						 bottom : window.innerHeight || document.documentElement.clientHeight,
						 right  : window.innerWidth || document.documentElement.clientWidth
					 };
					 const rect = element.getBoundingClientRect();
					 return intersectedRectangle(shadowWindow,rect);
				 };

				 function uniteSets(setA, setB) {
					 const union = new Set(setA);
					 for (let element of setB) {
						 union.add(element);
					 };
					 return union;
				 };

				 function minusSets(setA, setB) {
					 return new Set(Array.from(setA).filter(item => !setB.has(item)));
				 };

				 function intersectedRectangle (rectOne, rectTwo) {
					 return !(
						 rectTwo.left >= rectOne.right ||
						 rectTwo.right <= rectOne.left ||
						 rectTwo.top >= rectOne.bottom ||
						 rectTwo.bottom <= rectOne.top
					 );
				 };

				 function getElementsByClassNameInShadow(root, className) {
					 let elements = [];

					 function traverse(node) {
						 if (node.getElementsByClassName) {
							 Array.from(node.getElementsByClassName(className)).forEach(el => elements.push(el));
						 };

						 node.childNodes.forEach(child => {
							 if (child.shadowRoot) {
								 traverse(child.shadowRoot);
							 };
							 traverse(child);
						 });
					 };

					 traverse(root);
					 return elements;
				 };

				 function getElementsBySelectorInShadow(root, selector) {
					 let elements = [];

					 function traverse(node) {
						 if (node.querySelectorAll) {
							 elements.push(...node.querySelectorAll(selector));
						 };

						 node.childNodes.forEach(child => {
							 if (child.shadowRoot) {
								 traverse(child.shadowRoot);
							 };
							 traverse(child);
						 });
					 };

					 traverse(root);
					 return elements;
				 };

				 if (window.DevRevSDK.mobilesdk.domChanged || !(sdk.autoDetected || sdk.elements || sdk.domNodes || sdk.blacklist || sdk.whitelist)) {
					 window.DevRevSDK.mobilesdk.domChanged = false;

					 sdk.autoDetected = Array.prototype.slice.call(getElementsBySelectorInShadow(document,
						 "input[type='date'], input[type='datetime'], input[type='datetime-local'], input[type='month'], " +
						 "input[type='text'], input[type='email'], input[type='number'], input[type='tel'], " +
						 "input[type='file'], input[type='password'] "
					 ));

					 sdk.elements  = Array.prototype.slice.call(getElementsByClassNameInShadow(document,'devrev-mask'));
					 sdk.domNodes  = Array.prototype.slice.call(getElementsBySelectorInShadow(document,window.DevRevSDK.mobilesdk.PIISelectors));
					 sdk.blacklist = Array.prototype.concat(sdk.elements, sdk.domNodes);
					 sdk.whitelist = Array.prototype.slice.call(getElementsByClassNameInShadow(document,'devrev-unmask'));
				 };

				 let allSecuredElements = () => {
					 let allSecured = new Set();
					 allSecured = uniteSets(allSecured, new Set(sdk.autoDetected));
					 allSecured = minusSets(allSecured, new Set(sdk.whitelist));
					 allSecured = uniteSets(allSecured, new Set(sdk.blacklist));
					 return allSecured;
				 };

				 const rects = Array.from(allSecuredElements()).filter(function (element) {
					 return !isElementHidden(element) && isInViewport(element);
				 }).flatMap(e => e.getBoundingClientRect());

				 return serializeRects(rects);
			 };

			 sdk.snapshotInformation = function () {
				 return JSON.stringify({
					 isScrolling     : !!window.DevRevSDK.mobilesdk.monitor.isScrolling,
					 isZooming       : !!window.DevRevSDK.mobilesdk.monitor.isZooming,
					 locationsToMask : window.DevRevSDK.mobilesdk.getBlockedLocations(),
					 windowWidth     : window.innerWidth || document.documentElement.clientWidth
				 });
			 };

			 sdk.updatePIISelectors = function (queryString) {
				 window.DevRevSDK.mobilesdk.PIISelectors = queryString;
				 window.DevRevSDK.mobilesdk.domChanged = true;
			 };
			 sdk.clearPIISelectors = function (queryString) {
				 window.DevRevSDK.mobilesdk.PIISelectors = undefined;
				 window.DevRevSDK.mobilesdk.domChanged = true;
			 };
		 };
		 applySDK();
	 };
 } )();
 if (window.DevRevSDK && window.DevRevSDK.mobilesdk) {
	 window.DevRevSDK.mobilesdk.snapshotInformation();
 };
