/**
 * Phone-local remix/generate helpers. No network, no fetch.
 * Used by the Android standalone shell and Node contract tests.
 */
(function (root) {
  const MAX_FREE_LONG_EDGE = 1216;
  const MAX_FREE_STEPS = 28;
  const MAX_FREE_PIXELS = 1024 * 1024;
  const SUBJECT_RE = /^(\d+)(girl|girls|boy|boys|other|others)$/;
  const SUBJECT_KIND = {
    girl: "girl",
    girls: "girl",
    boy: "boy",
    boys: "boy",
    other: "other",
    others: "other",
  };
  const GENERIC_IDENTITY = {
    "1girl": 1,
    "1boy": 1,
    female_focus: 1,
    male_focus: 1,
    original_character: 1,
  };

  function asObject(value) {
    if (value && typeof value === "object" && !Array.isArray(value)) return value;
    if (typeof value !== "string") return {};
    const text = value.trim();
    if (!text || text === "[object Object]") return {};
    try {
      const parsed = JSON.parse(text);
      return parsed && typeof parsed === "object" && !Array.isArray(parsed) ? parsed : {};
    } catch (_) {
      return {};
    }
  }

  function parseComment(raw) {
    if (raw && typeof raw === "object" && !Array.isArray(raw)) return raw;
    return asObject(raw);
  }

  function normalizeComment(comment) {
    if (!comment || typeof comment !== "object") return {};
    const normalized = Object.assign({}, comment);
    ["v4_prompt", "v4_negative_prompt"].forEach((key) => {
      if (key in normalized) normalized[key] = asObject(normalized[key]);
    });
    return normalized;
  }

  function effectiveComment(aiJson) {
    const source = asObject(aiJson);
    const parsed = parseComment(source.Comment);
    return normalizeComment(Object.keys(parsed).length ? parsed : source);
  }

  function splitPromptTags(value) {
    return String(value || "")
      .replace(/\n/g, ",")
      .split(",")
      .map((item) => item.trim())
      .filter(Boolean);
  }

  function tagKey(value) {
    return String(value || "").trim().toLowerCase().replace(/_/g, " ");
  }

  function uniqueTags(values) {
    const seen = Object.create(null);
    const out = [];
    (values || []).forEach((item) => {
      const text = String(item || "").trim();
      const key = tagKey(text);
      if (!text || seen[key]) return;
      seen[key] = 1;
      out.push(text);
    });
    return out;
  }

  const GENDER_TOKENS = {
    "1girl": "female",
    "2girls": "female",
    "3girls": "female",
    "4girls": "female",
    "5girls": "female",
    "6girls": "female",
    girl: "female",
    girls: "female",
    female: "female",
    "female focus": "female",
    "girls only": "female",
    "1boy": "male",
    "2boys": "male",
    "3boys": "male",
    "4boys": "male",
    boy: "male",
    boys: "male",
    male: "male",
    "male focus": "male",
    "boys only": "male",
    "1other": "unknown",
    "2others": "unknown",
  };
  const BODY_RE = /\b(petite|slim|plump|muscular|tall|short|loli|oppai|flat chest|small breasts|medium breasts|large breasts|huge breasts|breasts|penis|pussy|ass|thighs|hips|belly|stomach)\b/;
  const APPEAR_RE = /\b(\w+ hair|\w+ eyes|long hair|short hair|very long hair|twintails|ponytail|ahoge|bangs|animal ears|fox ears|cat ears|wolf ears|horse ears|dragon horns|horns|halo|tail|fox tail|wolf tail|wings|pointy ears|dark skin|pale skin|freckles|heterochromia)\b/;
  const ACTION_RE = /\b(standing|sitting|lying|kneeling|walking|running|looking|holding|grabbing|smile|smiling|closed mouth|open mouth|from side|from above|from below|cowboy shot|upper body|full body|portrait|close-up|spread legs|bent over|arms up|hands? up|solo focus)\b/;
  const SCENE_RE = /\b(classroom|street|indoors|outdoors|night|day|sunset|moonlit|sky|city|forest|beach|bedroom|kitchen|office|school|watercolor|oil painting|anime coloring|cinematic lighting|depth of field|blurry background|simple background|white background|scenery)\b/;
  const CHAR_SUFFIX_RE = /^(.+?)(?:_\(([^)]+)\)|\s*\(([^)]+)\))$/;
  const WEIGHT_RE = /^-?\d+(?:\.\d+)?::(.+?)(?:::)?$/;

  function weightedInner(token) {
    const text = String(token || "").trim();
    const match = WEIGHT_RE.exec(text);
    return match ? String(match[1] || "").trim() : text;
  }

  function genderFromText(key) {
    const compact = String(key || "").replace(/\s+/g, " ").trim();
    if (GENDER_TOKENS[compact] || GENDER_TOKENS[compact.replace(/\s+/g, "")]) {
      return GENDER_TOKENS[compact] || GENDER_TOKENS[compact.replace(/\s+/g, "")];
    }
    if (/\b(1boy|2boys|3boys|4boys|boys only|male focus|male|boy|man|men)\b/.test(compact)) return "male";
    if (/\b(1girl|2girls|3girls|4girls|girls only|female focus|female|girl|woman|women|lady)\b/.test(compact)) return "female";
    return "";
  }
PLACEHOLDER_TOO_LARGE_SEE_NOTE
