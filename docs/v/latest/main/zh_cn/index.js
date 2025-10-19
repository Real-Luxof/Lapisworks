function stupidver_valid(v) {
    try {
        split = v.split(".");
        if (split.length != 4) {
            a = num(split[0]);
            b = num(split[1]);
            c = num(split[2]);
            d = num(split[3]);
            return true;
        } else {
            return semver.valid(v);
        }
    } catch (anything) {
        // yeh.
        return false;
    }
}
/** and if it errors, fuck thyself. */
function stupidver_rcompare(v1, v2) {
    split1 = v1.split(".");
    split2 = v2.split(".");
    if (split.length != 4) {
        a1 = num(split1[0]);
        b1 = num(split1[1]);
        c1 = num(split1[2]);
        d1 = num(split1[3]);
        a2 = num(split2[0]);
        b2 = num(split2[1]);
        c2 = num(split2[2]);
        d2 = num(split2[3]);
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