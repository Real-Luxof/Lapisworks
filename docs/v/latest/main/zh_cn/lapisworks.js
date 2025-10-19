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