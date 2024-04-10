import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class StringUtils {
    private static final Map<String, String> entityEscapeMap = new HashMap<>();
    private static final Map<String, String> escapeEntityMap = new HashMap<>();

    private static final String FOLDER_SEPARATOR = "/";

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static final String TOP_PATH = "..";

    private static final String CURRENT_PATH = ".";

    private static final char EXTENSION_SEPARATOR = '.';

    private static final String _BR = "<br/>";

    private static final List<String> COMMON_WORDS = new ArrayList<>();

    static {
        String[][] entities = {{"&nbsp;", "&#160;", " "}, {"&iexcl;", "&#161;", "¡"}, {"&cent;", "&#162;", "¢"}, {"&pound;", "&#163;", "£"}, {"&curren;", "&#164;", "¤"}, {"&yen;", "&#165;", "¥"}, {"&brvbar;", "&#166;", "¦"}, {"&sect;", "&#167;", "§"}, {"&uml;", "&#168;", "¨"}, {"&copy;", "&#169;", "©"}, {"&ordf;", "&#170;", "ª"}, {"&laquo;", "&#171;", "«"}, {"&not;", "&#172;", "¬"}, {"&shy;", "&#173;", "­"}, {"&reg;", "&#174;", "®"}, {"&macr;", "&#175;", "¯"}, {"&deg;", "&#176;", "°"}, {"&plusmn;", "&#177;", "±"}, {"&sup2;", "&#178;", "²"}, {"&sup3;", "&#179;", "³"}, {"&acute;", "&#180;", "´"}, {"&micro;", "&#181;", "µ"}, {"&para;", "&#182;", "¶"}, {"&middot;", "&#183;", "·"}, {"&cedil;", "&#184;", "¸"}, {"&sup1;", "&#185;", "¹"}, {"&ordm;", "&#186;", "º"}, {"&raquo;", "&#187;", "»"}, {"&frac14;", "&#188;", "¼"}, {"&frac12;", "&#189;", "½"}, {"&frac34;", "&#190;", "¾"}, {"&iquest;", "&#191;", "¿"}, {"&Agrave;", "&#192;", "À"}, {"&Aacute;", "&#193;", "Á"}, {"&Acirc;", "&#194;", "Â"}, {"&Atilde;", "&#195;", "Ã"}, {"&Auml;", "&#196;", "Ä"}, {"&Aring;", "&#197;", "Å"}, {"&AElig;", "&#198;", "Æ"}, {"&Ccedil;", "&#199;", "Ç"}, {"&Egrave;", "&#200;", "È"}, {"&Eacute;", "&#201;", "É"}, {"&Ecirc;", "&#202;", "Ê"}, {"&Euml;", "&#203;", "Ë"}, {"&Igrave;", "&#204;", "Ì"}, {"&Iacute;", "&#205;", "Í"}, {"&Icirc;", "&#206;", "Î"}, {"&Iuml;", "&#207;", "Ï"}, {"&ETH;", "&#208;", "Ð"}, {"&Ntilde;", "&#209;", "Ñ"}, {"&Ograve;", "&#210;", "Ò"}, {"&Oacute;", "&#211;", "Ó"}, {"&Ocirc;", "&#212;", "Ô"}, {"&Otilde;", "&#213;", "Õ"}, {"&Ouml;", "&#214;", "Ö"}, {"&times;", "&#215;", "×"}, {"&Oslash;", "&#216;", "Ø"}, {"&Ugrave;", "&#217;", "Ù"}, {"&Uacute;", "&#218;", "Ú"}, {"&Ucirc;", "&#219;", "Û"}, {"&Uuml;", "&#220;", "Ü"}, {"&Yacute;", "&#221;", "Ý"}, {"&THORN;", "&#222;", "Þ"}, {"&szlig;", "&#223;", "ß"}, {"&agrave;", "&#224;", "à"}, {"&aacute;", "&#225;", "á"}, {"&acirc;", "&#226;", "â"}, {"&atilde;", "&#227;", "ã"}, {"&auml;", "&#228;", "ä"}, {"&aring;", "&#229;", "å"}, {"&aelig;", "&#230;", "æ"}, {"&ccedil;", "&#231;", "ç"}, {"&egrave;", "&#232;", "è"}, {"&eacute;", "&#233;", "é"}, {"&ecirc;", "&#234;", "ê"}, {"&euml;", "&#235;", "ë"}, {"&igrave;", "&#236;", "ì"}, {"&iacute;", "&#237;", "í"}, {"&icirc;", "&#238;", "î"}, {"&iuml;", "&#239;", "ï"}, {"&eth;", "&#240;", "ð"}, {"&ntilde;", "&#241;", "ñ"}, {"&ograve;", "&#242;", "ò"}, {"&oacute;", "&#243;", "ó"}, {"&ocirc;", "&#244;", "ô"}, {"&otilde;", "&#245;", "õ"}, {"&ouml;", "&#246;", "ö"}, {"&divide;", "&#247;", "÷"}, {"&oslash;", "&#248;", "ø"}, {"&ugrave;", "&#249;", "ù"}, {"&uacute;", "&#250;", "ú"}, {"&ucirc;", "&#251;", "û"}, {"&uuml;", "&#252;", "ü"}, {"&yacute;", "&#253;", "ý"}, {"&thorn;", "&#254;", "þ"}, {"&yuml;", "&#255;", "ÿ"}, {"&fnof;", "&#402;", "ƒ"}, {"&Alpha;", "&#913;", "Α"}, {"&Beta;", "&#914;", "Β"}, {"&Gamma;", "&#915;", "Γ"}, {"&Delta;", "&#916;", "Δ"}, {"&Epsilon;", "&#917;", "Ε"}, {"&Zeta;", "&#918;", "Ζ"}, {"&Eta;", "&#919;", "Η"}, {"&Theta;", "&#920;", "Θ"}, {"&Iota;", "&#921;", "Ι"}, {"&Kappa;", "&#922;", "Κ"}, {"&Lambda;", "&#923;", "Λ"}, {"&Mu;", "&#924;", "Μ"}, {"&Nu;", "&#925;", "Ν"}, {"&Xi;", "&#926;", "Ξ"}, {"&Omicron;", "&#927;", "Ο"}, {"&Pi;", "&#928;", "Π"}, {"&Rho;", "&#929;", "Ρ"}, {"&Sigma;", "&#931;", "Σ"}, {"&Tau;", "&#932;", "Τ"}, {"&Upsilon;", "&#933;", "Υ"}, {"&Phi;", "&#934;", "Φ"}, {"&Chi;", "&#935;", "Χ"}, {"&Psi;", "&#936;", "Ψ"}, {"&Omega;", "&#937;", "Ω"}, {"&alpha;", "&#945;", "α"}, {"&beta;", "&#946;", "β"}, {"&gamma;", "&#947;", "γ"}, {"&delta;", "&#948;", "δ"}, {"&epsilon;", "&#949;", "ε"}, {"&zeta;", "&#950;", "ζ"}, {"&eta;", "&#951;", "η"}, {"&theta;", "&#952;", "θ"}, {"&iota;", "&#953;", "ι"}, {"&kappa;", "&#954;", "κ"}, {"&lambda;", "&#955;", "λ"}, {"&mu;", "&#956;", "μ"}, {"&nu;", "&#957;", "ν"}, {"&xi;", "&#958;", "ξ"}, {"&omicron;", "&#959;", "ο"}, {"&pi;", "&#960;", "π"}, {"&rho;", "&#961;", "ρ"}, {"&sigmaf;", "&#962;", "ς"}, {"&sigma;", "&#963;", "σ"}, {"&tau;", "&#964;", "τ"}, {"&upsilon;", "&#965;", "υ"}, {"&phi;", "&#966;", "φ"}, {"&chi;", "&#967;", "χ"}, {"&psi;", "&#968;", "ψ"}, {"&omega;", "&#969;", "ω"}, {"&thetasym;", "&#977;", "ϑ"}, {"&upsih;", "&#978;", "ϒ"}, {"&piv;", "&#982;", "ϖ"}, {"&bull;", "&#8226;", "•"}, {"&hellip;", "&#8230;", "…"}, {"&prime;", "&#8242;", "′"}, {"&Prime;", "&#8243;", "″"}, {"&oline;", "&#8254;", "‾"}, {"&frasl;", "&#8260;", "⁄"}, {"&weierp;", "&#8472;", "℘"}, {"&image;", "&#8465;", "ℑ"}, {"&real;", "&#8476;", "ℜ"}, {"&trade;", "&#8482;", "™"}, {"&alefsym;", "&#8501;", "ℵ"}, {"&larr;", "&#8592;", "←"}, {"&uarr;", "&#8593;", "↑"}, {"&rarr;", "&#8594;", "→"}, {"&darr;", "&#8595;", "↓"}, {"&harr;", "&#8596;", "↔"}, {"&crarr;", "&#8629;", "↵"}, {"&lArr;", "&#8656;", "⇐"}, {"&uArr;", "&#8657;", "⇑"}, {"&rArr;", "&#8658;", "⇒"}, {"&dArr;", "&#8659;", "⇓"}, {"&hArr;", "&#8660;", "⇔"}, {"&forall;", "&#8704;", "∀"}, {"&part;", "&#8706;", "∂"}, {"&exist;", "&#8707;", "∃"}, {"&empty;", "&#8709;", "∅"}, {"&nabla;", "&#8711;", "∇"}, {"&isin;", "&#8712;", "∈"}, {"&notin;", "&#8713;", "∉"}, {"&ni;", "&#8715;", "∋"}, {"&prod;", "&#8719;", "∏"}, {"&sum;", "&#8721;", "∑"}, {"&minus;", "&#8722;", "−"}, {"&lowast;", "&#8727;", "∗"}, {"&radic;", "&#8730;", "√"}, {"&prop;", "&#8733;", "∝"}, {"&infin;", "&#8734;", "∞"}, {"&ang;", "&#8736;", "∠"}, {"&and;", "&#8743;", "∧"}, {"&or;", "&#8744;", "∨"}, {"&cap;", "&#8745;", "∩"}, {"&cup;", "&#8746;", "∪"}, {"&int;", "&#8747;", "∫"}, {"&there4;", "&#8756;", "∴"}, {"&sim;", "&#8764;", "∼"}, {"&cong;", "&#8773;", "≅"}, {"&asymp;", "&#8776;", "≈"}, {"&ne;", "&#8800;", "≠"}, {"&equiv;", "&#8801;", "≡"}, {"&le;", "&#8804;", "≤"}, {"&ge;", "&#8805;", "≥"}, {"&sub;", "&#8834;", "⊂"}, {"&sup;", "&#8835;", "⊃"}, {"&sube;", "&#8838;", "⊆"}, {"&supe;", "&#8839;", "⊇"}, {"&oplus;", "&#8853;", "⊕"}, {"&otimes;", "&#8855;", "⊗"}, {"&perp;", "&#8869;", "⊥"}, {"&sdot;", "&#8901;", "⋅"}, {"&lceil;", "&#8968;", "⌈"}, {"&rceil;", "&#8969;", "⌉"}, {"&lfloor;", "&#8970;", "⌊"}, {"&rfloor;", "&#8971;", "⌋"}, {"&lang;", "&#9001;", "〈"}, {"&rang;", "&#9002;", "〉"}, {"&loz;", "&#9674;", "◊"}, {"&spades;", "&#9824;", "♠"}, {"&clubs;", "&#9827;", "♣"}, {"&hearts;", "&#9829;", "♥"}, {"&diams;", "&#9830;", "♦"}, {"&quot;", "&#34;", "\""}, {"&amp;", "&#38;", "&"}, {"&lt;", "&#60;", "<"}, {"&gt;", "&#62;", ">"}, {"&OElig;", "&#338;", "Œ"}, {"&oelig;", "&#339;", "œ"}, {"&Scaron;", "&#352;", "Š"}, {"&scaron;", "&#353;", "š"}, {"&Yuml;", "&#376;", "Ÿ"}, {"&circ;", "&#710;", "ˆ"}, {"&tilde;", "&#732;", "˜"}, {"&ensp;", "&#8194;", " "}, {"&emsp;", "&#8195;", " "}, {"&thinsp;", "&#8201;", " "}, {"&zwnj;", "&#8204;", "‌"}, {"&zwj;", "&#8205;", "‍"}, {"&lrm;", "&#8206;", "‎"}, {"&rlm;", "&#8207;", "‏"}, {"&ndash;", "&#8211;", "–"}, {"&mdash;", "&#8212;", "—"}, {"&lsquo;", "&#8216;", "‘"}, {"&rsquo;", "&#8217;", "’"}, {"&sbquo;", "&#8218;", "‚"}, {"&ldquo;", "&#8220;", "“"}, {"&rdquo;", "&#8221;", "”"}, {"&bdquo;", "&#8222;", "„"}, {"&dagger;", "&#8224;", "†"}, {"&Dagger;", "&#8225;", "‡"}, {"&permil;", "&#8240;", "‰"}, {"&lsaquo;", "&#8249;", "‹"}, {"&rsaquo;", "&#8250;", "›"}, {"&euro;", "&#8364;", "€"}};


        for (String[] entity : entities) {
            entityEscapeMap.put(entity[2], entity[0]);
            escapeEntityMap.put(entity[0], entity[2]);
            escapeEntityMap.put(entity[1], entity[2]);
        }
        String[] words = {"brave", "president", "stocking", "calculate", "profession", "chance", "attraction", "compete", "religion", "bribery", "furnish", "roll", "knock", "slope", "event", "ribbon", "joke", "cage", "trick", "right", "inquire", "essential", "hindrance", "answer", "regard", "theater", "meet", "few", "touch", "native", "scrape", "quality", "somebody", "wherever", "forbid", "steady", "moral", "behavior", "imaginary", "net", "I", "commerce", "relief", "inquiry", "guard", "conversation", "grey", "mention", "trial", "pity", "purple", "tribe", "a", "suffer", "join", "enjoy", "new", "red", "guess", "library", "guest", "advice", "fancy", "basin", "basis", "root", "drop", "change", "exception", "tool", "lead", "leaf", "capital", "qualification", "march", "medical", "height", "rope", "basic", "description", "reputation", "plaster", "composition", "suddenbasic", "motion", "guilt", "station", "watch", "automatic", "fat", "luck", "yesterday", "canvas", "far", "resist", "double", "fan", "jaw", "borrow", "much", "country", "lean", "roof", "procession", "speech", "tremble", "delicate", "often", "active", "tough", "barber", "brass", "treasury", "make", "room", "raw", "justice", "motherly", "ray", "indoor", "connect", "distant", "horse", "occasion", "organize", "through", "education", "disturb", "generous", "business", "possible", "gradual", "especially", "cottage", "treasure", "hinder", "universal", "alike", "population", "mail", "equal", "gray", "wicked", "main", "hello", "dine", "entire", "which", "advantage", "wooden", "amusement", "headache", "female", "anyone", "freeze", "royal", "inside", "peace", "castle", "brown", "peculiar", "suspicious", "rock", "haste", "help", "rank", "village", "social", "report", "empty", "border", "probablepublic", "hill", "elect", "rot", "support", "resign", "health", "row", "scent", "rob", "rod", "suppose", "avenue", "mend", "weave", "silence", "bravery", "needle", "scene", "branch", "western", "upon", "indeed", "broad", "roar", "joy", "road", "express", "space", "build", "decision", "distance", "fiction", "insect ocean", "rather", "note", "melt", "water", "plasterarch", "faith", "spade", "omit", "club", "square", "strengthen", "laughter", "telephone", "nose", "polite", "dish", "apply", "single", "envy", "furniture", "where", "every", "beast", "shave", "almost", "postpone", "gentle", "rare", "experiment", "wonder", "action", "none", "pray", "dirt", "hire", "eye", "wrap", "joint", "damage", "opposition", "here", "finger", "trunk", "imitate", "apple", "dress", "vain", "interference", "penny", "mere", "violent", "decay", "polish", "chairman", "scale", "expansion", "noon", "chest", "circle", "grip", "disease", "while", "sight", "rate", "below", "drum", "develop", "lonely", "puzzle", "death agree", "dive", "into", "problem", "offer", "instrument", "bottle", "daily", "grammar", "anxious", "remedy", "rid", "comfortcommittee", "cultivate", "tender", "scientific", "beard", "clothe", "spread", "advance", "sugar", "faint", "allowance", "appear", "understand", "discussion", "donkey", "everyday", "flow", "cave", "production", "temperature", "hasten", "although", "shelter", "hide", "drink", "bundle", "year", "throat", "underneath", "cowardice", "cheat", "sour", "nowhere", "laugh", "under", "soul", "suggest", "soup", "system", "terrible", "bottom", "favor", "cheap", "behave", "admire", "press", "machinery", "rug", "sort", "advise", "expensive", "run", "iron", "absolute", "substance", "adventure", "strike", "sore", "life", "truth", "pick", "annoy", "sensitive", "citizen", "pinch", "trust", "learning", "thirst", "young", "admit", "charm", "sticky", "narrow", "remain", "basket", "congratulate", "nut", "insult", "garden", "world", "reflect", "baggage", "progress", "arise", "thicken", "become", "perform", "scientist", "must", "curious", "practical", "care", "card", "misery", "demand", "literary", "orange", "mouth", "spare", "agriculture", "cart", "forest", "youth", "chalk", "rub", "several", "case", "thumb", "early", "quarter", "attempt", "song", "gallon", "mouse", "insure", "liar", "child", "verb", "performance", "precious", "dirty", "grateful", "question", "mercy", "beneath", "strength", "engineer", "multiply", "soon", "general", "representative", "discontent", "charge", "effect", "pretend", "high", "daylight", "lodge", "very", "birth", "surface", "sad", "everything", "zero", "feather", "conscience", "elsewhere", "draw", "mill", "sacrifice", "camp", "pint", "stock", "drive", "milk", "mile", "mild", "pink", "industry", "exchange", "confusion", "again", "like", "not", "many", "solid", "relieve", "nor", "probable", "now", "start", "satisfaction", "prize", "say", "saw", "shield", "strict", "dance", "some", "outside", "stream", "translation", "backward", "cape", "line", "valley", "fortunate", "escape", "bathe", "robbery", "straighten", "upset", "mine", "pile", "mind", "district", "end", "corner", "male", "drag", "already", "confidence", "limb", "remind", "broken", "arrange", "chair", "chain", "brother", "tour", "belong", "remark", "overcome", "courage", "egg", "heat", "skin", "bicycle", "heap", "mass", "hear", "heal", "police", "toward", "splendid", "head", "chief", "sure", "cake", "uppermost", "cheer", "dream", "coward", "permission", "mark", "hearing", "soften", "homecoming", "thick", "lift", "town", "golden", "repetition", "soil", "calm", "decide", "call", "slave", "paper", "state", "veil", "restaurant", "pipe", "interfere", "hanging", "because", "another", "flatten", "full", "glass", "ripen", "chimney", "blow", "check", "inch", "century", "addition", "teach", "soft", "noise", "amuse", "beside", "address", "shoulder", "thief", "special", "pastry", "group", "average", "miss", "educate", "total", "need", "mountain", "photograph", "arch", "freedom", "fish", "lessen", "visit", "wait", "over", "free", "search", "she", "slippery", "partner", "something", "black", "ordinary", "once", "death", "parent", "number", "attract", "apart", "smoke", "further", "soap", "view", "explain", "discovery", "deserve", "before", "screw", "ocean", "oil", "trouble", "division", "wage", "could", "weaken", "wet", "skirt", "thorough", "sand", "stair", "firm", "anxiety", "living", "boat", "canal", "fire", "stain", "represent", "sock", "sit", "history", "sir", "dissatisfaction", "possession", "nothing", "deepen", "scenery", "clever", "bound", "body", "stupid", "impossible", "same", "enough", "taste", "completion", "boundary", "who", "close", "doctor", "pause", "suspicion", "slight", "passage", "opinion", "everybody", "light", "key", "think", "get", "possessor", "sting", "apology", "grave", "eager", "agent", "sale", "cough", "near", "imitation", "saddle", "better", "tooth", "neat", "against", "example", "walk", "wall", "coarse", "thing", "pound", "salt", "invention", "sew", "reflection", "merry", "neck", "advertise", "department", "set", "sake", "today", "retire", "former", "plural", "electrician", "throw", "woolen", "wake", "experience", "employee", "discipline", "stamp", "objection", "society", "funny", "sea", "still", "roast", "win", "deceit", "message", "see", "stand", "off", "suit", "accustom", "handkerchief", "why", "stomach", "sorrow", "scorn", "they", "manager", "yield", "collar", "curl", "concern", "court", "receipt", "actor", "homemade", "council", "cure", "operation", "break", "convenience", "deliver", "bread", "apparatus", "combine", "weekend", "only", "anybody", "worth", "knife", "pigeon", "ring", "screen", "lesson", "sow", "heart", "church", "heighten", "son", "include", "upright", "gas", "skill", "confident", "gay", "then", "delivery", "hurrah", "gap", "different", "subject", "moment", "plenty", "influence", "protect", "hammer", "dollar", "usual", "disregard", "clock", "prejudice", "oar", "flight", "translator", "proposal", "ripe", "tobacco", "healthy", "within", "design", "cause", "count", "save", "second", "permit", "nursery", "study", "niece", "middle", "level", "sky", "sadden", "gather", "someone", "official", "childhood", "protection", "republic", "such", "doubt", "suck", "heavy", "machine", "relative", "comparison", "whichever", "mother", "stuff", "manufacture", "operate", "dust", "thin", "bridge", "stiff", "this", "cotton", "stick", "earnest", "wax", "way", "cheese", "from", "believe", "stage", "network", "war", "civilize", "army", "risk", "real", "staff", "poverty", "poet", "duty", "rise", "able", "mixed", "read", "tailor", "various", "poem", "receive", "between", "application", "audience", "standard", "important", "lipstick", "everlasting", "print", "ambition", "worse", "away", "loosen", "digestion", "bleed", "wrong", "intend", "worry", "guide", "handle", "wood", "fight", "arrow", "pride", "classify", "danger", "admission", "forward", "juice", "snow", "resistance", "happen", "wool", "brain", "piece", "serve", "turn", "honor", "neighborhood", "kitchen", "favorite", "moonlight", "declare", "price", "afraid", "supper", "crime", "warmth", "shower", "split", "own", "complain", "fellow", "face", "wheel", "common", "breakfast", "bucket", "next", "explosive", "daughter", "figure", "habit", "punctual", "happy", "motor", "shilling", "possess", "fur", "whenever", "notice", "hair", "direct", "fade", "news", "absence", "theatrical", "fault", "angle", "passenger", "familiar", "lung", "fact", "preference", "mixture", "prompt", "competitor", "decrease", "agree", "scold", "fear", "relate", "breadth", "detail", "both", "rake", "seize", "darken", "telegraph", "loose", "after", "slavery", "bribe", "marry", "fruit", "rotten", "association", "certainty", "coffee", "relation", "white", "horizon", "poison", "fun", "size", "salesman", "pearl", "that", "inform", "encourage", "explosion", "anger", "than", "foreign", "result", "model", "humble", "opposite", "tube", "brick", "about", "well", "photography", "sun", "impulse", "owe", "disgust", "elder", "complete", "companion", "rail", "supply", "above", "feed", "severe", "rust", "confidential", "rain", "animal", "unite", "bowl", "feel", "rush", "dull", "nest", "promise", "descent", "punish", "whistle", "sauce", "variety", "notebook", "unity", "whoever", "grow", "grain", "service", "harvest", "descend", "claim", "fry", "tight", "blind", "sword", "out", "waste", "flash", "selfish", "quart", "for", "accept", "angry", "bring", "sailor", "cover", "ancient", "whisper", "fail", "criminal", "prevention", "false", "weather", "choose", "wine", "rude", "wind", "glad", "wing", "enclosure", "grass", "airplane", "preach", "compare", "minister", "prevent", "table", "pretense", "applause", "ceremony", "director", "deafen", "boil", "fair", "handshake", "hunger", "soldier", "edgeeducation", "tax", "boiling", "flesh", "language", "tap", "evil", "position", "kneel", "ground", "effective", "character", "particle", "title", "latter", "cloth", "judge", "union", "length", "rent", "modesty", "want", "desert", "edge", "responsible", "heaven", "nuisance", "wipe", "astonish", "visitor", "secretary", "snake", "cupboard", "article", "fall", "cloud", "steam", "wander", "lump", "old", "wish", "ruin", "wise", "fortune", "race", "educator", "fame", "anyway", "bold", "please", "grand", "duck", "custom", "work", "complicate", "worm", "murder", "accuse", "succeed", "letter", "wire", "discover", "class", "property", "sense", "tune", "cattle", "northern", "window", "fly", "clerk", "program", "company", "popular", "wave", "word", "even", "rest", "brush", "nowadays", "west", "friend", "ever", "efficient", "ride", "clay", "warn", "stop", "warm", "voice", "allow", "intention", "caution", "one", "recent", "pretty", "coast", "flame", "minute", "fit", "satisfactory", "officer", "during", "fix", "rule", "bone", "meat", "wash", "mean", "offense", "perfect", "fine", "find", "meal", "wheat", "office", "film", "profit", "with", "mistake", "rice", "desire", "matter", "rich", "separation", "fill", "steep", "moderate", "passage lawyer", "steel", "safety", "rubbish", "observe", "steer", "continue", "book", "confuse", "bright", "librarian", "particular", "tend", "party", "float", "time", "married", "refer", "tent", "sympathy", "salary", "bare", "these", "thunder", "else", "host", "conquer", "wide", "tail", "worship", "pencil", "bit", "shorten", "pocket", "bite", "urge", "nephew", "discuss", "wealth", "bitter", "sick", "each", "big", "uncle", "linen", "nail", "knowledge", "smooth", "queen", "rival", "dependent", "authority", "grind", "patient", "side", "recognize", "bath", "tell", "member", "stir", "razor", "hour", "morning", "till", "drown", "bird", "comfort", "base", "aunt", "public", "greed", "physical", "congratulation", "ditch", "anywhere", "envelope", "broadcast", "qualify", "fate", "hat", "kiss", "band", "friendship", "brighten", "bank", "gentleman", "hook", "sweeten", "crack", "cream", "tame", "sign", "refuse", "offend", "patriotic", "overflow", "hay", "distinguish", "factory", "day", "fast", "prefer", "large", "hope", "insurance", "bill", "measure", "membership", "powder", "exercise", "feeble", "farm", "cautious", "perfection", "ache", "invent", "coal", "paw", "name", "spoil", "altogether", "pay", "tonight", "complaint", "coat", "tall", "quarrel", "talk", "law", "music", "pad", "depend", "lay", "Christmas", "pan", "scarce", "neither", "therefore", "never", "great", "acid", "widen", "take", "there", "patience", "box", "bow", "boy", "bind", "cup", "govern", "appearance", "harbor", "reason", "somehow", "package", "otherwise", "hatred", "calculation", "stroke", "among", "smile", "everyone", "refresh", "carry", "wild", "bag", "numerous", "hold", "bad", "distribution", "shoot", "bay", "headdress", "horizontal", "creep", "scissors", "colony", "will", "ought", "bar", "newspaper", "pardon", "shore", "home", "accident", "empire", "proof", "holy", "cruel", "short", "clear", "thank", "clean", "unless", "fertile", "waiter", "listen", "hole", "stay", "creature", "dozen", "green", "beautiful", "advertisement", "star", "greet", "frequent", "extraordinary", "master", "pen", "omission", "per", "hollow", "pupil", "pet", "twist", "translate", "force", "cut", "efficiency", "settle", "avoidance", "wisdom", "spoon", "difficulty", "***", "since", "neighbor", "reproduction", "type", "honest", "hit", "family", "exact", "tear", "request", "calculator", "medicine", "argument", "loyal", "onto", "approve", "beg", "bed", "pleasure", "enter", "tire", "depth", "arrive", "dinner", "winter", "strong", "flood", "bless", "whole", "stiffen", "rabbit", "explore", "defeat", "grease", "appoint", "quick", "ease", "politician", "arrest", "path", "east", "conqueror", "despair", "building", "applaud", "political", "agreement", "play", "fatten", "sport", "motherhood", "past", "pig", "pass", "election", "convenient", "plan", "pin", "wife", "night", "stem", "proud", "earn", "also", "step", "shout", "madden", "nice", "manage", "yellow", "customer", "pot", "interruption", "seat", "interest", "adopt", "hunt", "lovely", "quantity", "along", "alone", "copy", "cool", "cook", "prove", "attentive", "waiting", "vote", "quiet", "blood", "always", "log", "temple", "obey", "inward", "conscious", "swear", "blade", "lot", "low", "surprise", "sweat", "critic", "ugly", "complex", "taxi", "god", "annoyance", "use", "difficult", "influential", "captain", "businessman", "kingdom", "best", "baby", "gold", "landlord", "moderation", "royalty", "whatever", "seed", "weed", "thorn", "come", "back", "divide", "comb", "seem", "material", "either", "presence", "feeling", "practice", "sorry", "south", "actress", "down", "defend", "week", "strap", "straw", "bend", "busy", "cow", "absent", "good", "meantime", "science", "mankind", "metal", "bush", "redden", "cultivation", "lunch", "leather", "crash", "control", "vessel", "imagine", "belief", "whose", "condition", "electric", "bury", "behind", "cost", "sweep", "difference", "bell", "failure", "battle", "sweet", "sound", "whom", "belt", "burn", "car", "cat", "cork", "can", "crush", "cap", "corn", "collector", "ornament", "attention", "enclose", "argue", "thus", "regular", "moreover", "camera", "approval", "regret", "you", "secrecy", "sacred", "interrupt", "English", "cry", "reference", "complication", "unit", "immediate", "anything", "actual", "increase", "weak", "wear", "monkey", "bent", "competition", "curve", "person", "heavenly", "excessive", "mechanism", "house", "people", "modest", "beyond", "existence", "avoid", "tree", "shadow", "dissatisfy", "pour", "businesslike", "loyalty", "musician", "blame", "tidy", "jealous", "goat", "extra", "cushion", "curtain", "gaiety", "funeral", "put", "bake", "block", "self", "outline", "modern", "excellence", "jewel", "sell", "persuasion", "tide", "just", "aloud", "silk", "ball", "extreme", "post", "let", "return", "friendly", "oppose", "framework", "gun", "mechanic", "conquest", "leg", "degree", "prison", "disrespect", "radio", "curse", "discomfort", "but", "send", "bus", "mystery", "buy", "amongst", "introduce", "travel", "drawer", "together", "preserve", "reproduce", "explode", "swell", "hurry", "cold", "nurse", "swing", "deceive", "connection", "tongue", "carriage", "disapprove", "literature", "paint", "sink", "strip", "trap", "jump", "gate", "picture", "formal", "season", "rejoice", "tray", "sing", "stretch", "match", "barrel", "ashamed", "north", "purpose", "solution", "upper", "affair", "pool", "lately", "exist", "invite", "university", "pressure", "lip", "merchant", "leadership", "coin", "poor", "dependence", "around", "begin", "lid", "tower", "organ", "quite", "towel", "instead", "first", "lie", "river", "hesitation", "solemn", "store", "threat", "college", "entertain", "lord", "storm", "until", "parcel", "deed", "story", "politics", "chemical", "thread", "sail", "government", "slow", "deep", "instant", "deer", "breath", "forgive", "engine", "plate", "he", "sleep", "universe", "confess", "ear", "voyage", "struggle", "aim", "paste", "butter", "persuade", "air", "eat", "artificial", "immense", "introduction", "hospital", "review", "go", "disappearance", "idea", "gift", "raise", "satisfy", "wrist", "servant", "excellent", "honesty", "berry", "extensive", "leave", "mad", "consider", "map", "current", "write", "sheep", "left", "do", "evening", "fasten", "man", "sheet", "bear", "look", "safe", "adoption", "may", "beat", "bean", "violence", "debt", "wreck", "mat", "operator", "gain", "vowel", "yes", "what", "beak", "beam", "lazy", "ago", "yet", "expression", "least", "normal", "driving", "stone", "spite", "abroad", "by", "dear", "long", "balance", "deal", "would", "autumn", "future", "any", "seldom", "dead", "deaf", "be", "victory", "priest", "reward", "saucer", "umbrella", "likely", "flavor", "learn", "grammatical", "burst", "bedroom", "and", "holiday", "lighten", "suggestion", "lend", "straight", "collect", "remember", "boast", "railroad", "garage", "correction", "destruction", "waist", "frequency", "husband", "recommend", "island", "sister", "pronunciation", "multiplication", "ice", "attractive", "swim", "trip", "defendant", "all", "sample", "parallel", "disappoint", "speed", "ahead", "glory", "at", "burn sun", "ounce", "as", "game", "memory", "color", "meeting", "manner", "forget", "growth", "nobody", "no", "street", "determine", "door", "sympathetic", "fixed", "inclusive", "of", "correct", "button", "ticket", "feast", "on", "pleasant", "whiten", "whip", "commercial", "move", "limit", "maybe", "or", "cliff", "widower", "board", "threaten", "simplicity", "committee", "month", "delay", "small", "expert", "plant", "fashion", "development", "most", "necessity", "surround", "across", "aside", "wound", "chicken", "solve", "adjustment", "harmony", "sharpen", "situation", "ideal", "true", "cultivator", "marriage", "product", "extension", "idle", "classification", "FALSE", "account", "sometime", "earth", "dismiss", "produce", "realize", "last", "sometimes", "recognition", "being", "cousin", "fever", "military", "temper", "love", "more", "when", "age", "value", "train", "noble", "credit", "spell", "live", "inn", "stove", "everywhere", "flat", "spill", "ink", "miserable", "loud", "flag", "liquid", "homework", "permanent", "late", "eastern", "lawyer", "virtue", "moon", "loss", "highway", "copper", "elephant", "improve", "beauty", "save", "father", "mineral", "extent", "if", "diamond", "suspect", "customary", "fright", "less", "those", "it", "besides", "ill", "afternoon", "extend", "silver", "old-fashioned", "destructive", "agency", "list", "devil", "in", "know", "rapid", "knot", "spend", "human", "lose", "opportunity", "hurt", "blue", "tighten", "necessary", "hotel", "local", "act", "ready", "dot", "spot", "strange", "nature", "compose", "humor", "place", "serious", "signature", "knee", "friendlypair", "rubber", "how", "international", "journey", "loan", "stripe", "content", "grace", "pure", "loaf", "ownership", "floor", "hot", "load", "elastic", "shade", "try", "we", "silent", "climb", "harm", "give", "choice", "simple", "sentence", "plow", "hardly", "collection", "desk", "date", "spring", "witness", "entrance", "easy", "widow", "fierce", "reply", "slide", "hard", "axe", "ability", "scratch", "outward", "park", "front", "employ", "reasonable", "up", "field", "replace", "rivalry", "expense", "pattern", "weapon", "string", "frighten", "dog", "part", "respect", "keep", "afford", "asleep", "to", "essence", "kick", "hall", "provide", "attend", "pull", "half", "round", "somewhere", "messenger", "crop", "amount", "mud", "student", "dark", "swallow", "welcome", "dare", "follow", "however", "verse", "shallow", "so", "width", "pump", "effort", "pale", "lake", "reduce", "nation", "weigh", "finish", "frame", "due", "handwriting", "natural", "toe", "weight", "trade", "sudden", "catch", "land", "signal", "repeat", "hang", "hand", "destroy", "liberty", "reach", "toy", "shut", "ton", "bunch", "lengthen", "plain", "too", "top", "confession", "ladder", "cent", "fellowship", "defense", "separate", "dry", "sincere", "track", "accord", "flour", "school", "lamp", "praise", "certain", "momentary", "success", "forth", "shell", "examine", "test", "tie", "excuse", "shake", "rescue", "governor", "shine", "excite", "tin", "insect", "tip", "whether", "city", "course", "center", "expect", "circular", "open", "damp", "page", "propose", "describe", "shelf", "arm", "flower", "fork", "form", "awkward", "art", "disagree", "bargain", "prepare", "without", "imaginative", "ambitious", "shall", "tempt", "obedient", "present", "record", "fence", "harden", "ash", "anyhow", "tired", "reserve", "ask", "cross", "smell", "shame", "might", "hesitate", "tomorrow", "pair", "ship", "neglect", "pain", "king", "kind", "delight", "private", "spit", "command", "money", "spin", "jealousy", "hut", "speak", "slip", "awake", "dictionary", "should", "pronounce", "mix", "scatter", "point", "valuable", "noun", "spirit", "attack", "shop", "hate", "meanwhile", "foot", "kill", "little", "show", "pack", "tea", "alive", "fool", "though", "woman", "power", "proper", "reduction", "repair", "shoe", "shirt", "except", "breathe", "obedience", "foolishfree", "shape", "lack", "rough", "market", "importance", "companionship", "food", "contain", "perhaps", "treat", "revenge", "other", "yard", "have", "crown", "direction", "excess", "object", "push", "fond", "share", "dip", "girl", "weekday", "order", "fresh", "die", "dig", "jelly", "crowd", "enemy", "error", "sharp", "descendant", "urgent", "origin", "lock", "the", "inventor", "fold", "shock", "decisive", "term", "summer", "lady", "secret", "disappear"};


        for (String s : words) {
            COMMON_WORDS.add(s);
        }
    }


    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }


    public static boolean hasText(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    public static boolean containsWhitespace(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    public static String trimWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
            buf.deleteCharAt(0);
        }
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }


    public static String trimLeadingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
            buf.deleteCharAt(0);
        }
        return buf.toString();
    }


    public static String trimLeadingCharacter(String str, char leadingCharacter) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && buf.charAt(0) == leadingCharacter) {
            buf.deleteCharAt(0);
        }
        return buf.toString();
    }


    public static String trimTrailingWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        while (buf.length() > 0 && Character.isWhitespace(buf.charAt(buf.length() - 1))) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }


    public static String trimAllWhitespace(String str) {
        if (!hasLength(str)) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str);
        int index = 0;
        while (buf.length() > index) {
            if (Character.isWhitespace(buf.charAt(index))) {
                buf.deleteCharAt(index);
                continue;
            }
            index++;
        }

        return buf.toString();
    }


    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        String lcStr = str.substring(0, prefix.length()).toLowerCase();
        String lcPrefix = prefix.toLowerCase();
        return lcStr.equals(lcPrefix);
    }


    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (str.endsWith(suffix)) {
            return true;
        }
        if (str.length() < suffix.length()) {
            return false;
        }

        String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
        String lcSuffix = suffix.toLowerCase();
        return lcStr.equals(lcSuffix);
    }


    public static int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0, pos = 0, idx = 0;
        while ((idx = str.indexOf(sub, pos)) != -1) {
            count++;
            pos = idx + sub.length();
        }
        return count;
    }


    public static String replace(String inString, String oldPattern, String newPattern) {
        if (inString == null) {
            return null;
        }
        if (oldPattern == null || newPattern == null) {
            return inString;
        }

        StringBuffer sbuf = new StringBuffer();

        int pos = 0;
        int index = inString.indexOf(oldPattern);

        int patLen = oldPattern.length();
        while (index >= 0) {
            sbuf.append(inString, pos, index);
            sbuf.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sbuf.append(inString.substring(pos));


        return sbuf.toString();
    }


    public static String delete(String inString, String pattern) {
        return replace(inString, pattern, "");
    }


    public static String deleteAny(String inString, String charsToDelete) {
        if (!hasLength(inString) || !hasLength(charsToDelete)) {
            return inString;
        }
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                out.append(c);
            }
        }
        return out.toString();
    }


    public static String quote(String str) {
        return (str != null) ? ("'" + str + "'") : null;
    }


    public static Object quoteIfString(Object obj) {
        return (obj instanceof String) ? quote((String) obj) : obj;
    }


    public static String unqualify(String qualifiedName) {
        return unqualify(qualifiedName, '.');
    }


    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
    }


    public static String capitalize(String str) {
        return changeFirstCharacterCase(str, true);
    }


    public static String uncapitalize(String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (str == null || str.length() == 0) {
            return str;
        }
        StringBuffer buf = new StringBuffer(str.length());
        if (capitalize) {
            buf.append(Character.toUpperCase(str.charAt(0)));
        } else {
            buf.append(Character.toLowerCase(str.charAt(0)));
        }
        buf.append(str.substring(1));
        return buf.toString();
    }


    public static String getFilename(String path) {
        if (path == null) {
            return null;
        }
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex != -1) ? path.substring(separatorIndex + 1) : path;
    }


    public static String getFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int sepIndex = path.lastIndexOf('.');
        return (sepIndex != -1) ? path.substring(sepIndex + 1) : null;
    }


    public static String stripFilenameExtension(String path) {
        if (path == null) {
            return null;
        }
        int sepIndex = path.lastIndexOf('.');
        return (sepIndex != -1) ? path.substring(0, sepIndex) : path;
    }


    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf("/");
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith("/")) {
                newPath = newPath + "/";
            }
            return newPath + relativePath;
        }
        return relativePath;
    }


    public static Locale parseLocaleString(String localeString) {
        String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
        String language = (parts.length > 0) ? parts[0] : "";
        String country = (parts.length > 1) ? parts[1] : "";
        String variant = "";
        if (parts.length >= 2) {


            int endIndexOfCountryCode = localeString.indexOf(country) + country.length();


            variant = trimLeadingWhitespace(localeString.substring(endIndexOfCountryCode));
            if (variant.startsWith("_")) {
                variant = trimLeadingCharacter(variant, '_');
            }
        }
        return (language.length() > 0) ? new Locale(language, country, variant) : null;
    }



    public static String[] toStringArray(Collection collection) {
        if (collection == null) {
            return null;
        }
        return (String[]) collection.toArray(new String[collection.size()]);
    }


    public static String[] split(String toSplit, String delimiter) {
        if (!hasLength(toSplit) || !hasLength(delimiter)) {
            return null;
        }
        int offset = toSplit.indexOf(delimiter);
        if (offset < 0) {
            return null;
        }
        String beforeDelimiter = toSplit.substring(0, offset);
        String afterDelimiter = toSplit.substring(offset + delimiter.length());
        return new String[]{beforeDelimiter, afterDelimiter};
    }


    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }


    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {
        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }


    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }


    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[]{str};
        }
        List<String> result = new ArrayList();
        if ("".equals(delimiter)) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int pos = 0;
            int delPos = 0;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }


    public static String[] commaDelimitedListToStringArray(String str) {
        return delimitedListToStringArray(str, ",");
    }


    public static Set commaDelimitedListToSet(String str) {
        Set<String> set = new TreeSet();
        String[] tokens = commaDelimitedListToStringArray(str);
        for (int i = 0; i < tokens.length; i++) {
            set.add(tokens[i]);
        }
        return set;
    }


    public static String[] splitBy(String str, char sp) {
        if (str == null) {
            return new String[0];
        }
        str = str.trim();
        if (str.equals("")) {
            return new String[0];
        }
        List<String> list = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        boolean inQuote = false;

        int len = str.length();
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (ch == '\\' && i + 1 < len) {
                i++;
                ch = str.charAt(i);
                sb.append(ch);
            } else if (sb.length() != 0 || (ch != ' ' && ch != '\t' && ch != '\r' && ch != '\n')) {

                if (i + 1 == len) {
                    if (ch == '"' && inQuote) {
                        inQuote = false;
                        String str1 = sb.toString().trim();
                        sb.delete(0, sb.length());
                        list.add(str1);
                        break;
                    }
                    if (ch != sp) {
                        sb.append(ch);
                        String str1 = sb.toString().trim();
                        sb.delete(0, sb.length());
                        list.add(str1);
                        break;
                    }
                    String s = sb.toString().trim();
                    sb.delete(0, sb.length());
                    list.add(s);
                    list.add("");
                    break;
                }
                if (ch == sp && !inQuote) {
                    String s = sb.toString().trim();
                    sb.delete(0, sb.length());
                    list.add(s);


                } else if (ch == '"' && sb.length() == 0 && str.indexOf('"', i + 1) > 0) {
                    inQuote = true;
                } else if (ch == '"' && inQuote) {
                    inQuote = false;
                } else {
                    sb.append(ch);
                }
            }
        }
        String[] ret = new String[list.size()];
        list.toArray(ret);
        return ret;
    }

    public static String escape(String original) {
        StringBuffer buf = new StringBuffer(original);
        escape(buf);
        return buf.toString();
    }

    public static void escape(StringBuffer original) {
        int index = 0;

        while (index < original.length()) {
            String escaped = entityEscapeMap.get(original.substring(index, index + 1));
            if (null != escaped) {
                original.replace(index, index + 1, escaped);
                index += escaped.length();
                continue;
            }
            index++;
        }
    }


    public static String unescape(String original) {
        StringBuffer buf = new StringBuffer(original);
        unescape(buf);
        return buf.toString();
    }

    public static void unescape(StringBuffer original) {
        int index = 0;
        int semicolonIndex = 0;


        while (index < original.length()) {
            index = original.indexOf("&", index);
            if (-1 == index) {
                break;
            }
            semicolonIndex = original.indexOf(";", index);
            if (-1 != semicolonIndex && 10 > semicolonIndex - index) {
                String escaped = original.substring(index, semicolonIndex + 1);
                String entity = escapeEntityMap.get(escaped);
                if (null != entity) {
                    original.replace(index, semicolonIndex + 1, entity);
                }
                index++;
            }
        }
    }


    public static String htmlDecode(String str) {
        String s = str;
        Pattern p = Pattern.compile("(?i)&#x?(\\d|[a-f]){1,4};");
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();
        int start = 0;
        while (m.find()) {
            sb.append(s, start, m.start());
            String m1 = m.group(1);
            if (m.group().toLowerCase().startsWith("&#x")) {
                sb.append((char) Integer.parseInt(m1, 16));
            } else {
                sb.append((char) Integer.parseInt(m1));
            }
            start = m.end();
        }
        sb.append(s.substring(start));
        return unescape(sb.toString());
    }


    public static String htmlShow(String str) {
        if (str == null) {
            return null;
        }

        str = replace("<", "&lt;", str);
        str = replace(" ", "&nbsp;", str);
        str = replace("\r\n", "<br/>", str);
        str = replace("\n", "<br/>", str);
        str = replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;", str);
        return str;
    }


    public static String toLength(String str, int length) {
        if (str == null) {
            return null;
        }
        if (length <= 0) {
            return "";
        }
        try {
            if ((str.getBytes("GBK")).length <= length) {
                return str;
            }
        } catch (Exception exception) {
        }

        StringBuffer buff = new StringBuffer();

        int index = 0;

        length -= 3;
        while (length > 0) {
            char c = str.charAt(index);
            if (c < '') {
                length--;
            } else {
                length--;
                length--;
            }
            buff.append(c);
            index++;
        }
        buff.append("...");
        return buff.toString();
    }


    public static boolean isInteger(String str) {
        if (str == null || str.trim().length() == 0) return false;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isNumber(String str) {
        if (str == null || str.length() == 0) return false;
        for (int i = 0; i < str.length(); ) {
            char c = str.charAt(i);
            if ((c >= '0' && c <= '9') || (
                    i == 0 && (c == '-' || c == '+'))) {
                i++;
                continue;
            }
            return false;
        }

        return true;
    }


    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(str).matches();
    }


    public static boolean isChinese(String str) {
        Pattern pattern = Pattern.compile("[Α-￥]+$");
        return pattern.matcher(str).matches();
    }


    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }


    public static boolean isPrime(int x) {
        if (x <= 7 && (
                x == 2 || x == 3 || x == 5 || x == 7)) {
            return true;
        }
        int c = 7;
        if (x % 2 == 0)
            return false;
        if (x % 3 == 0)
            return false;
        if (x % 5 == 0)
            return false;
        int end = (int) Math.sqrt(x);
        while (c <= end) {
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 4;
            if (x % c == 0) {
                return false;
            }
            c += 6;
            if (x % c == 0) {
                return false;
            }
            c += 2;
            if (x % c == 0) {
                return false;
            }
            c += 6;
        }
        return true;
    }


    public static int newRandomInt(int min, int max) {
        int result = min + (new Double(Math.random() * (max - min))).intValue();

        return result;
    }

    public static String newRandomNumStr(int length) {
        StringBuffer sb = new StringBuffer();
        sb.append(newRandomInt(1, 10));
        while (--length > 0) {
            sb.append(newRandomInt(0, 10));
        }


        return sb.toString();
    }


    public static long newRandomLong(long min, long max) {
        long result = min + (new Double(Math.random() * (max - min))).longValue();

        return result;
    }

    public static byte[] newRandomByteArray(int size) {
        if (size < 0) return null;
        byte[] bytes = new byte[size];
        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) (newRandomInt(0, 256) & 0xFF);
        }
        return bytes;
    }


    public static int[] newRandomIntArray(int min, int max, int size) {
        int[] result = new int[size];

        int arraySize = max - min;
        int[] intArray = new int[arraySize];
        int i;
        for (i = 0; i < intArray.length; i++) {
            intArray[i] = i + min;
        }

        for (i = 0; i < size; i++) {
            int c = newRandomInt(min, max - i);
            int index = c - min;
            swap(intArray, index, arraySize - 1 - i);
            result[i] = intArray[arraySize - 1 - i];
        }

        return result;
    }

    private static void swap(int[] array, int x, int y) {
        int temp = array[x];
        array[x] = array[y];
        array[y] = temp;
    }


    public static double newRandomDouble(double min, double max) {
        double result = min + Math.random() * (max - min);
        return result;
    }


    public static char newRandomChar() {
        int firstChar = 33;
        int lastChar = 126;
        char result = (char) newRandomInt(firstChar, lastChar + 1);
        return result;
    }


    public static char newRandomPrintableChar() {
        int number = newRandomInt(0, 62);
        int zeroChar = 48;
        int nineChar = 57;
        int aChar = 97;
        int zChar = 122;
        int AChar = 65;
        int ZChar = 90;


        if (number < 10) {
            char result = (char) newRandomInt(zeroChar, nineChar + 1);
            return result;
        }
        if (number >= 10 && number < 36) {
            char result = (char) newRandomInt(AChar, ZChar + 1);
            return result;
        }
        if (number >= 36 && number < 62) {
            char result = (char) newRandomInt(aChar, zChar + 1);
            return result;
        }
        return Character.MIN_VALUE;
    }


    public static String newRandomString(int length) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; i++) {
            result.append(newRandomChar());
        }
        return result.toString();
    }

    public static String newRandomXStr(int length) {
        StringBuffer sb = new StringBuffer();
        double p = 0.625D;
        for (int i = 0; i < length; i++) {
            if (Math.random() <= p) {
                sb.append((char) newRandomInt(48, 58));
            } else {
                sb.append((char) newRandomInt(65, 71));
            }
        }
        return sb.toString();
    }


    public static String newRandomPrintableString(int length) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; i++) {
            result.append(newRandomPrintableChar());
        }
        return result.toString();
    }

    public static String newRandomWord() {
        if (COMMON_WORDS.isEmpty()) return newRandomWord(newRandomInt(3, 12));
        return COMMON_WORDS.get(newRandomInt(0, COMMON_WORDS.size()));
    }

    public static String newRandomSentence(int n) {
        StringBuffer sb = new StringBuffer(newRandomWord());
        for (int i = 1; i < n; i++) {
            sb.append(' ').append(newRandomWord());
        }
        return sb.toString();
    }

    public static String newRandomWord(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            char a;
            int t = newRandomInt(0, 26);


            if (Math.random() > 0.8D) {
                a = (char) (65 + t);
            } else {
                a = (char) (97 + t);
            }
            sb.append(a);
        }
        return sb.toString();
    }

    public static String byteArrayToString(byte[] bts, String delimiter) {
        if (bts == null || bts.length == 0) return "";
        if (delimiter == null) delimiter = "";
        StringBuffer sb = new StringBuffer();
        for (byte b : bts) {
            int t = b;
            if (t < 0) t += 256;
            if (t < 16) sb.append(0);
            sb.append(Integer.toHexString(t));
            sb.append(delimiter);
        }
        if (delimiter.length() > 0 && sb.length() > 0) sb.delete(sb.length() - delimiter.length(), sb.length());
        return sb.toString();
    }


    public static String newRandomTWID() {
        String[] ss = {"A 台北市 10", "B 台中市 11", "C 基隆市 12", "D 台南市 13", "E 高雄市 14", "F 台北县 15", "G 宜兰县 16", "H 桃园县 17", "I 嘉义市 34", "J 新竹县 18", "K 苗栗县 19", "L 台中县 20", "M 南投县 21", "N 彰化县 22", "O 新竹市 35", "P 云林县 23", "Q 嘉义县 24", "R 台南县 25", "S 高雄县 26", "T 屏东县 27", "U 花莲县 28", "V 台东县 29", "W 金门县 32", "X 澎湖县 30", "Y 阳明山 31", "Z 连江县 33"};


        int n = newRandomInt(0, ss.length);
        StringBuffer sb = new StringBuffer();
        sb.append(ss[n].charAt(0));
        if (Math.random() > 0.5D) {
            sb.append(2);
        } else {
            sb.append(1);
        }

        sb.append(newRandomInt(1000000, 10000000));

        String dizhiduiying = ss[n].substring(ss[n].length() - 2);
        int sum = Integer.parseInt(dizhiduiying.substring(0, 1)) + 9 * Integer.parseInt(dizhiduiying.substring(1, 2));
        for (int i = 1; i < 9; i++) {
            sum += Integer.parseInt(sb.substring(i, i + 1)) * (9 - i);
        }
        int mod = sum % 10;
        sb.append(10 - mod);
        return sb.toString();
    }

    public static String parseUnicode(String src) {
        if (src == null) return null;
        int start = 0;
        Pattern p = Pattern.compile("(?i)\\\\u([\\d|\\w]{4})");
        Matcher m = p.matcher(src);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            sb.append(src, start, m.start());
            try {
                char a = (char) Integer.parseInt(m.group(1), 16);
                sb.append(a);
            } catch (Exception e) {
                sb.append(m.group(0));
            }
            start = m.end();
        }
        sb.append(src.substring(start));
        return sb.toString();
    }

    public static String newRandomPassword(int min, int max) {
        String password = newRandomWord();
        if (Math.random() > 0.9D && password.length() < max - 4) password = password + newRandomWord();
        if (Math.random() > 0.6D && password.length() < max - 4)
            password = password + newRandomWord(newRandomInt(2, 4));
        if (Math.random() > 0.3D && password.length() < max - 4) password = password + newRandomInt(0, 1000);
        if (password.length() < min)
            password = password + newRandomPrintableString(min + (max - min) / 2 - password.length());
        if (password.length() > max) password = password.substring(max - newRandomInt(0, max - min));
        if (!password.matches(".*?\\d+.*?")) {
            int in = newRandomInt(0, password.length());
            password = password.substring(0, in) + newRandomInt(0, 10) + password.substring(in + 1);
        }
        return password;
    }


    public static String bytesToHex(byte[] bytes) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            int t = bytes[i];
            if (t < 0)
                t += 256;
            sb.append(hexDigits[t >>> 4]);
            sb.append(hexDigits[t % 16]);
        }
        return sb.toString();
    }


    public static int calculateStringDistance(String strA, String strB) {
        short lenA = (short) strA.length();
        short lenB = (short) strB.length();
        short[][] c = new short[lenA + 1][lenB + 1];


        for (int k = 0; k < lenA; k++)
            c[k][lenB] = (short) (lenA - k);
        for (int j = 0; j < lenB; j++)
            c[lenA][j] = (short) (lenB - j);
        c[lenA][lenB] = 0;
        for (int i = lenA - 1; i >= 0; i--) {
            for (int m = lenB - 1; m >= 0; m--) {
                if (strB.charAt(m) == strA.charAt(i)) {
                    c[i][m] = c[i + 1][m + 1];
                } else {
                    c[i][m] = (short) (Math.min(Math.min(c[i][m + 1], c[i + 1][m]), c[i + 1][m + 1]) + 1);
                }
            }
        }

        return c[0][0];
    }


    public static String getLCString(String strA, String strB) {
        int len1 = strA.length();
        int len2 = strB.length();
        int maxLen = (len1 > len2) ? len1 : len2;
        int[] max = new int[maxLen];
        int[] maxIndex = new int[maxLen];
        int[] c = new int[maxLen];
        int i;
        for (i = 0; i < len2; i++) {
            for (int k = len1 - 1; k >= 0; k--) {
                if (strB.charAt(i) == strA.charAt(k)) {
                    if (i == 0 || k == 0) {
                        c[k] = 1;
                    } else {
                        c[k] = c[k - 1] + 1;
                    }
                } else {
                    c[k] = 0;
                }

                if (c[k] > max[0]) {
                    max[0] = c[k];
                    maxIndex[0] = k;
                    for (int m = 1; m < maxLen; m++) {
                        max[m] = 0;
                        maxIndex[m] = 0;
                    }
                } else if (c[k] == max[0]) {
                    for (int m = 1; m < maxLen; m++) {
                        if (max[m] == 0) {
                            max[m] = c[k];
                            maxIndex[m] = k;

                            break;
                        }
                    }
                }
            }
        }

        for (int j = 0; j < maxLen; j++) {
            if (max[j] > 0) {

                StringBuffer sb = new StringBuffer();
                for (i = maxIndex[j] - max[j] + 1; i <= maxIndex[j]; i++) {
                    sb.append(strA.charAt(i));
                }

                return sb.toString();
            }
        }
        return "";
    }

    public static void main(String[] args) {
        String str1 = "adbbda1234";
        String str2 = "adbbdf1234sa";
        System.out.println(getLCString(str1, str2));
        System.out.println(calculateStringDistance(str1, str2));
    }
}


/* Location:              C:\Users\fred\Downloads\bet-server-1.0-SNAPSHOT\BOOT-INF\lib\bet-common-1.0.0-SNAPSHOT.jar!\com\chief\ww\\util\StringUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */