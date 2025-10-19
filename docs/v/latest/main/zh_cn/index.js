"use strict";
const speeds = [0, 0.25, 0.5, 1, 2, 4];
const scrollThreshold = 100;
const rfaQueue = [];
const colorCache = new Map();
function getColorRGB(ctx, str) {
  if (!colorCache.has(str)) {
    ctx.fillStyle = str;
    ctx.clearRect(0, 0, 1, 1);
    ctx.fillRect(0, 0, 1, 1);
    const imgData = ctx.getImageData(0, 0, 1, 1);
    colorCache.set(str, imgData.data);
  }
  return colorCache.get(str);
}
function startAngle(str) {
  switch (str) {
    case "east":
      return 0;
    case "north_east":
      return 1;
    case "north_west":
      return 2;
    case "west":
      return 3;
    case "south_west":
      return 4;
    case "south_east":
      return 5;
    default:
      return 0;
  }
}
function offsetAngle(str) {
  switch (str) {
    case "w":
      return 0;
    case "q":
      return 1;
    case "a":
      return 2;
    case "s":
      return 3;
    case "d":
      return 4;
    case "e":
      return 5;
    default:
      return -1;
  }
}
export function initializeElem(canvas) {
  const str = canvas.dataset.string;
  let angle = startAngle(canvas.dataset.start);
  const perWorld = canvas.dataset.perWorld === "True";
  // build geometry
  const points = [[0, 0]];
  let lastPoint = points[0];
  let minPoint = lastPoint,
    maxPoint = lastPoint;
  for (const ch of "w" + str) {
    const addAngle = offsetAngle(ch);
    if (addAngle < 0) continue;
    angle = (angle + addAngle) % 6;
    const trueAngle = (Math.PI / 3) * angle;
    const [lx, ly] = lastPoint;
    const newPoint = [lx + Math.cos(trueAngle), ly - Math.sin(trueAngle)];
    points.push(newPoint);
    lastPoint = newPoint;
    const [mix, miy] = minPoint;
    minPoint = [Math.min(mix, newPoint[0]), Math.min(miy, newPoint[1])];
    const [max, may] = maxPoint;
    maxPoint = [Math.max(max, newPoint[0]), Math.max(may, newPoint[1])];
  }
  const size = Math.min(canvas.width, canvas.height) * 0.8;
  const scale =
    size /
    Math.max(3, Math.max(maxPoint[1] - minPoint[1], maxPoint[0] - minPoint[0]));
  const center = [
    (minPoint[0] + maxPoint[0]) * 0.5,
    (minPoint[1] + maxPoint[1]) * 0.5,
  ];
  const truePoints = points.map((p) => [
    canvas.width * 0.5 + scale * (p[0] - center[0]),
    canvas.height * 0.5 + scale * (p[1] - center[1]),
  ]);
  let uniqPoints = [];
  l1: for (const point of truePoints) {
    for (const pt of uniqPoints) {
      if (
        Math.abs(point[0] - pt[0]) < 0.00001 &&
        Math.abs(point[1] - pt[1]) < 0.00001
      ) {
        continue l1;
      }
    }
    uniqPoints.push(point);
  }
  // rendering code
  const speed = 0.0025;
  const context = canvas.getContext("2d");
  const negaProgress = -3;
  let progress = 0;
  let scrollTimeout = 1e309;
  let speedLevel = 3;
  let speedIncrement = 0;
  function speedScale() {
    return speeds[speedLevel];
  }
  const style = getComputedStyle(canvas);
  const getProp = (n) => style.getPropertyValue(n);
  const tick = (dt) => {
    scrollTimeout += dt;
    if (canvas.offsetParent === null) return;
    const strokeStyle = getProp("--path-color");
    const strokeVisitedStyle = getProp("--visited-path-color");
    const startDotStyle = getProp("--start-dot-color");
    const dotStyle = getProp("--dot-color");
    const movDotStyle = getProp("--moving-dot-color");
    const strokeWidth = scale * +getProp("--line-scale");
    const dotRadius = scale * +getProp("--dot-scale");
    const movDotRadius = scale * +getProp("--moving-dot-scale");
    const pauseScale = scale * +getProp("--pausetext-scale");
    const bodyBg = scale * +getProp("--pausetext-scale");
    const darkMode = +getProp("--dark-mode");
    const bgColors = getColorRGB(
      context,
      getComputedStyle(document.body).backgroundColor
    );
    if (!perWorld) {
      progress +=
        speed * dt * (progress > 0 ? speedScale() : Math.sqrt(speedScale()));
    }
    if (progress >= truePoints.length - 1) {
      progress = negaProgress;
    }
    let ix = Math.floor(progress),
      frac = progress - ix,
      core = null,
      fadeColor = 0;
    if (ix < 0) {
      const rawFade = (2 * progress) / negaProgress - 1;
      fadeColor = 1 - Math.abs(rawFade);
      context.strokeStyle = rawFade > 0 ? strokeVisitedStyle : strokeStyle;
      ix = rawFade > 0 ? truePoints.length - 2 : 0;
      frac = +(rawFade > 0);
    } else {
      context.strokeStyle = strokeVisitedStyle;
    }
    const [lx, ly] = truePoints[ix];
    const [rx, ry] = truePoints[ix + 1];
    core = [lx + (rx - lx) * frac, ly + (ry - ly) * frac];
    context.clearRect(0, 0, canvas.width, canvas.height);
    context.beginPath();
    context.lineWidth = strokeWidth;
    context.moveTo(truePoints[0][0], truePoints[0][1]);
    for (let i = 1; i < ix + 1; i++) {
      context.lineTo(truePoints[i][0], truePoints[i][1]);
    }
    context.lineTo(core[0], core[1]);
    context.stroke();
    context.beginPath();
    context.strokeStyle = strokeStyle;
    context.moveTo(core[0], core[1]);
    for (let i = ix + 1; i < truePoints.length; i++) {
      context.lineTo(truePoints[i][0], truePoints[i][1]);
    }
    context.stroke();
    for (let i = 0; i < uniqPoints.length; i++) {
      context.beginPath();
      context.fillStyle = i == 0 && !perWorld ? startDotStyle : dotStyle;
      const radius = i == 0 && !perWorld ? movDotRadius : dotRadius;
      context.arc(uniqPoints[i][0], uniqPoints[i][1], radius, 0, 2 * Math.PI);
      context.fill();
    }
    if (!perWorld) {
      context.beginPath();
      context.fillStyle = movDotStyle;
      context.arc(core[0], core[1], movDotRadius, 0, 2 * Math.PI);
      context.fill();
    }
    if (fadeColor) {
      context.fillStyle = `rgba(${bgColors[0]}, ${bgColors[1]}, ${bgColors[2]}, ${fadeColor})`;
      context.fillRect(0, 0, canvas.width, canvas.height);
    }
    if (scrollTimeout <= 2000) {
      context.fillStyle = `rgba(200, 200, 200, ${
        (2000 - scrollTimeout) / 1000
      })`;
      context.font = `${pauseScale}px sans-serif`;
      context.fillText(
        // these variables are filled by Jinja
        // slightly scuffed, but it works for now
        speedScale() ? `${speedScale()}x` : "暂停",
        0.2 * scale,
        canvas.height - 0.2 * scale
      );
    }
  };
  rfaQueue.push(tick);
  // scrolling input
  if (!perWorld) {
    canvas.addEventListener("wheel", (ev) => {
      speedIncrement += ev.deltaY;
      const oldSpeedLevel = speedLevel;
      if (speedIncrement >= scrollThreshold) {
        speedLevel--;
      } else if (speedIncrement <= -scrollThreshold) {
        speedLevel++;
      }
      if (oldSpeedLevel != speedLevel) {
        speedIncrement = 0;
        speedLevel = Math.max(0, Math.min(speeds.length - 1, speedLevel));
        scrollTimeout = 0;
      }
      ev.preventDefault();
    });
  }
}
export function hookLoad(elem) {
  let init = false;
  const canvases = elem.querySelectorAll("canvas");
  elem.addEventListener("toggle", (a) => {
    if (!init) {
      canvases.forEach(initializeElem);
      init = true;
    }
  }, {once: true});
}
document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".details-collapsible").forEach(hookLoad);
  function tick(prevTime, time) {
    const dt = time - prevTime;
    for (const q of rfaQueue) {
      q(dt);
    }
    requestAnimationFrame((t) => tick(time, t));
  }
  requestAnimationFrame((t) => tick(t, t));
});"use strict";
import semver from 'https://cdn.jsdelivr.net/npm/semver@7.5.4/+esm';
// these are filled by Jinja
const RELATIVE_SITE_URL = "../../../..";
const VERSION = "latest/main";
const MINECRAFT_VERSION = "1.20.1";
const FULL_VERSION = "1.5.6.9.1.6.dev0";
const LANG = "zh_cn";
const SHOW_DROPDOWN_MINECRAFT_VERSION = `true` === "true";
const DROPDOWN_MINECRAFT_TEMPLATE = "Minecraft {version}";
// https://stackoverflow.com/a/61634647
function replaceTemplate(template, replacements) {
  return template.replace(
    /{(\w+)}/g,
    (placeholderWithDelimiters, placeholderWithoutDelimiters) =>
    replacements.hasOwnProperty(placeholderWithoutDelimiters) ?
      replacements[placeholderWithoutDelimiters] : placeholderWithDelimiters
  );
}
let cycleNodes = [];
function hookLoad(elem) {
  elem.addEventListener("toggle", () => {
    cycleNodes = document.querySelectorAll(".details-collapsible[open] .cycle-textures");
    if (elem.hasAttribute("open")) {
      for (const child of cycleNodes) {
        setEnabledMultiTexture(child, cycleIndex);
      }
    }
  });
}
function hookToggle(elem) {
  const details = Array.from(
    document.querySelectorAll("details." + elem.dataset.target)
  );
  elem.addEventListener("click", () => {
    if (details.some((x) => x.open)) {
      details.forEach((x) => (x.open = false));
    } else {
      details.forEach((x) => (x.open = true));
    }
  });
}
const params = new URLSearchParams(document.location.search);
function hookSpoiler(elem) {
  if (params.get("nospoiler") !== null) {
    elem.classList.add("unspoilered");
  } else {
    const thunk = (ev) => {
      if (!elem.classList.contains("unspoilered")) {
        ev.preventDefault();
        ev.stopImmediatePropagation();
        elem.classList.add("unspoilered");
      }
      elem.removeEventListener("click", thunk);
    };
    elem.addEventListener("click", thunk);
    if (elem instanceof HTMLAnchorElement) {
      const href = elem.getAttribute("href");
      if (href.startsWith("#")) {
        elem.addEventListener("click", () =>
          document
            .getElementById(href.substring(1))
            .querySelector(".spoilered")
            .classList.add("unspoilered")
        );
      }
    }
  }
}
let startTime = null;
function hookSyncAnimations(elem) {
  elem.addEventListener("animationstart", (e) => {
    for (const anim of e.target.getAnimations()) {
      if (startTime == null) {
        startTime = anim.startTime;
      } else {
        anim.startTime = startTime;
      }
    }
  })
}
let currentGaslight = 0;
let intersectingGaslights = 0;
let lastLookTimeMs = 0;
function startGaslighting(timeMs) {
  let newGaslight = Math.round(20 * (timeMs - lastLookTimeMs) / 1000);
  if (newGaslight >= 40) {
    currentGaslight = newGaslight - 40;
  }
  for (const elem of gaslightNodes) {
    setEnabledMultiTexture(elem, currentGaslight);
  }
}
function stopGaslighting(timeMs) {
  lastLookTimeMs = timeMs;
  for (const elem of gaslightNodes) {
    setEnabledMultiTexture(elem, null);
  }
}
let isFirstIntersectionEvent = true;
function hookIntersectionObserver(entries) {
  const wasLooking = intersectingGaslights > 0;
  let earliestStartMs = Number.MAX_VALUE;
  let latestStopMs = 0;
  for (const entry of entries) {
    if (entry.isIntersecting) {
      intersectingGaslights++;
      earliestStartMs = Math.min(earliestStartMs, entry.time);
    } else {
      if (!isFirstIntersectionEvent) intersectingGaslights--;
      latestStopMs = Math.max(latestStopMs, entry.time);
    }
  }
  isFirstIntersectionEvent = false;
  intersectingGaslights = Math.max(intersectingGaslights, 0);
  const isLooking = intersectingGaslights > 0;
  if (!wasLooking && isLooking) {
    startGaslighting(earliestStartMs);
  } else if (wasLooking && !isLooking) {
    stopGaslighting(latestStopMs);
  }
}
function hookVisibilityChange() {
  const time = performance.now();
  if (document.visibilityState === "visible") {
    startGaslighting(time);
  } else {
    stopGaslighting(time);
  }
}
let gaslightNodes;
let cycleIndex = 0;
let cycleTimeoutID;
function setEnabledMultiTexture(elem, index) {
  Array.from(elem.children).forEach((child, i) => {
    if (index !== null && i === (index % elem.children.length)) {
      child.classList.add("multi-texture-active");
    } else {
      child.classList.remove("multi-texture-active");
    }
  });
}
function doCycleTexturesForever() {
  cycleIndex += 1;
  for (const elem of cycleNodes) {
    setEnabledMultiTexture(elem, cycleIndex);
  }
  cycleTimeoutID = setTimeout(doCycleTexturesForever, 2000);
}
// Creates an element in the form `<li><a href=${href}>${text}</a></li>`
function dropdownItem(text, href) {
  let a = document.createElement("a");
  a.href = href;
  a.textContent = text;
  let li = document.createElement("li");
  li.appendChild(a);
  return li;
}
function versionDropdownItem(sitemap, versionKey) {
  const {defaultPath, langPaths, markers, defaultLang} = sitemap[versionKey];
  const {version, full_version, minecraft_version} = markers[LANG] ?? markers[defaultLang];
  // link to the current language if available, else link to the default language
  let path;
  if (langPaths.hasOwnProperty(LANG)) {
    path = langPaths[LANG];
  } else {
    path = defaultPath;
  }
  const li = dropdownItem(version, RELATIVE_SITE_URL + path);
  if (full_version === FULL_VERSION && versionKey == VERSION && minecraft_version == MINECRAFT_VERSION) {
    li.classList.add("disabled");
  }
  return li;
}
function versionDropdownItems(sitemap, versions) {
  return versions.map((version) => (
    versionDropdownItem(sitemap, version)
  ));
}
function dropdownSeparator() {
  let li = document.createElement("li");
  li.className = "divider";
  li.setAttribute("role", "separator");
  return li;
}
function dropdownHeader(text) {
  let li = document.createElement("li");
  li.className = "dropdown-header";
  li.textContent = text;
  return li;
}
function dropdownSubmenu(text, items) {
  let li = dropdownItem(text, "#");
  li.className = "dropdown-submenu";
  let ul = document.createElement("ul");
  ul.className = "dropdown-menu";
  ul.append(...items);
  li.appendChild(ul);
  return li;
}
// Like array.filter(predicate), but also returns the items which didn't match the filter.
function partition(array, predicate) {
  let matched = [];
  let unmatched = [];
  array.forEach((value, index) => {
    if (predicate(value, index, array)) {
      matched.push(value);
    } else {
      unmatched.push(value);
    }
  });
  return [matched, unmatched];
}
function sortSitemapVersions(unsortedVersions) {
  let [versions, branches] = partition(unsortedVersions, (v) => semver.valid(v) != null);
  // branches ascending, versions descending
  // eg. ["dev", "main"], ["0.10.0", "0.9.0"]
  branches.sort();
  versions.sort(semver.rcompare);
  return [branches, versions];
}
// Fills the version dropdown menus and the "old version" message.
function addDropdowns(sitemap) {
  let isOldVersion, dropdownItems, langNames, langPaths;
  if (SHOW_DROPDOWN_MINECRAFT_VERSION) {
    let minecraftVersions = Object.keys(sitemap);
    minecraftVersions.sort(semver.rcompare);
    let [_, currentVersions] = sortSitemapVersions(Object.keys(sitemap[MINECRAFT_VERSION]));
    isOldVersion = currentVersions.slice(1).includes(VERSION);
    dropdownItems = [];
    for (const minecraftVersion of minecraftVersions) {
      const subSitemap = sitemap[minecraftVersion];
      const [branches, versions] = sortSitemapVersions(Object.keys(subSitemap));
      dropdownItems.push(
        dropdownHeader(replaceTemplate(
          DROPDOWN_MINECRAFT_TEMPLATE,
          {version: minecraftVersion},
        )),
        ...versionDropdownItems(subSitemap, versions),
      );
      // honestly, i shouldn't be allowed to write javascript
      if (branches.length > 1) {
        dropdownItems.push(
          dropdownSubmenu(
            "latest/...", // TODO: this really shouldn't be hardcoded
            versionDropdownItems(subSitemap, branches).map(item => {
              item.firstChild.textContent = item.firstChild.textContent.replace(/^latest\//, "");
              return item;
            }),
          ),
        );
      } else if (branches.length == 1) {
        dropdownItems.push(
          versionDropdownItem(subSitemap, branches[0]),
        );
      }
    }
    langNames = sitemap[MINECRAFT_VERSION][VERSION].langNames;
    langPaths = sitemap[MINECRAFT_VERSION][VERSION].langPaths;
  } else { // legacy sitemap, not using minecraft versions
    let [branches, versions] = sortSitemapVersions(Object.keys(sitemap));
    isOldVersion = versions.slice(1).includes(VERSION);
    dropdownItems = [
      ...versionDropdownItems(sitemap, versions),
      dropdownSeparator(),
      ...versionDropdownItems(sitemap, branches),
    ];
    langNames = sitemap[VERSION].langNames;
    langPaths = sitemap[VERSION].langPaths;
  }
  // reveal the "old version" message if this page is a version number, but not the latest one
  // this isn't a dropdown, but it's here since we have the data anyway
  if (isOldVersion) {
    document.getElementById("old-version-notice").classList.remove("hidden")
  }
  // versions
  document.getElementById("version-dropdown").append(...dropdownItems);
  // languages for the current version
  const langs = Object.keys(langPaths).sort();
  document.getElementById("lang-dropdown").append(
    ...langs.map((lang) => dropdownItem(langNames[lang], RELATIVE_SITE_URL + langPaths[lang])),
  );
  // return sitemap for chaining, i guess
  return sitemap
}
document.addEventListener("DOMContentLoaded", () => {
  // fetch the sitemap from the root and use it to generate the navbar
  fetch(`${RELATIVE_SITE_URL}/meta/${SHOW_DROPDOWN_MINECRAFT_VERSION ? "sitemap-minecraft" : "sitemap"}.json`)
    .then(r => r.json())
    .then(addDropdowns)
    .catch(e => console.error(e))
  document.querySelectorAll(".details-collapsible").forEach(hookLoad);
  document.querySelectorAll("a.toggle-link").forEach(hookToggle);
  document.querySelectorAll(".spoilered").forEach(hookSpoiler);
  document.querySelectorAll(".animated-sync").forEach(hookSyncAnimations);
  doCycleTexturesForever();
  $(function () {
    $('[data-toggle="tooltip"]').tooltip()
  });
  $(".cycle-textures > .texture")
  .on("mouseenter", () => { // start hover
    if (cycleTimeoutID != null) {
      clearTimeout(cycleTimeoutID);
    }
  })
  .on("mouseleave", () => { // stop hover
    cycleTimeoutID = setTimeout(doCycleTexturesForever, 1000);
  });
  gaslightNodes = document.querySelectorAll(".gaslight-textures");
  for (const elem of gaslightNodes) {
    setEnabledMultiTexture(elem, 0);
  }
  const observer = new IntersectionObserver(hookIntersectionObserver, {
    rootMargin: "32px 32px 32px 32px",
  });
  gaslightNodes.forEach((elem) => observer.observe(elem));
  document.addEventListener("visibilitychange", hookVisibilityChange);
});
function stupidver_valid(v) {
    console.log("checking if " + v + " is valid.");
    try {
        split = v.split(".");
        console.log("split has been made and is " + split + ".");
        if (split.length != 4) {
            a = num(split[0]);
            b = num(split[1]);
            c = num(split[2]);
            d = num(split[3]);
            console.log("valid!");
            return true;
        } else {
            console.log("falling back to semver");
            return semver.valid(v);
        }
    } catch (anything) {
        console.log("invalid!");
        // yeh.
        return false;
    }
}
/** and if it errors, fuck thyself. */
function stupidver_rcompare(v1, v2) {
    console.log("comparing " + v1 + " and " + v2 + ".");
    split1 = v1.split(".");
    console.log("v1 is split to " + split1 + ".");
    split2 = v2.split(".");
    console.log("v2 is split to " + split2 + ".");
    if (split.length != 4) {
        console.log("inting v1.");
        a1 = num(split1[0]);
        b1 = num(split1[1]);
        c1 = num(split1[2]);
        d1 = num(split1[3]);
        console.log("inting v2.");
        a2 = num(split2[0]);
        b2 = num(split2[1]);
        c2 = num(split2[2]);
        d2 = num(split2[3]);
        console.log("comparing.");
        if (a1 > a2) {
            return -1;
        } else if (a1 < a2) {
            return 1;
        } else if (b1 > b2) {
            return -1;
        } else if (b1 < b2) {
            return 1;
        } else if (c1 > c2) {
            return -1;
        } else if (c1 < c2) {
            return 1;
        } else if (d1 > d2) {
            return -1;
        } else if (d1 < d2) {
            return 1;
        } else {
            return 0;
        }
    } else {
        console.log("faling back to semver.")
        return semver.rcompare(v1, v2);
    }
}
// why in the fuck did og programmer use 2-space indentation
// also i lowkey need to do all this to change 2 things lmao
function sortSitemapVersions(unsortedVersions) {
    let [versions, branches] = partition(unsortedVersions, (v) => stupidver_valid(v) != null);
    // branches ascending, versions descending
    // eg. ["dev", "main"], ["0.10.0", "0.9.0"]
    branches.sort();
    versions.sort(stupidver_rcompare);
    return [branches, versions];
}