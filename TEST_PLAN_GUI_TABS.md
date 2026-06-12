# GUI Tabs – Test Plan (Smoke / Sanity / Regression)

এই ডকুমেন্টে নিচের ৯টি ক্লাসের জন্য টেস্ট প্ল্যান দেওয়া হলো:
`SearchEmployeeTab`, `DeleteEmployeeTab`, `ViewEmployeeTab`, `PayslipTab`,
`LoginTab`, `WelcomeTab`, `RefreshTab`, `PayrollTabbedGUI`, `UIHelper`.

## ১. যা automated হলো (extracted logic)

`AddEmployeeTab` / `UpdateEmployeeTab`-এর মতোই, এই তিনটি ক্লাস থেকে pure (no
Swing/JDBC) logic আলাদা করে নতুন ক্লাস বানানো হয়েছে, যেগুলোর জন্য JUnit5 +
Cucumber টেস্ট লেখা হয়েছে এবং `mvn clean test` / `mvn clean verify` দিয়ে
চালানো যাবে:

| Tab | Extracted class | JUnit test | Cucumber feature |
|---|---|---|---|
| `LoginTab` | `AuthService` | `AuthServiceTest` | `login_auth.feature` |
| `SearchEmployeeTab` | `SearchQueryHelper` | `SearchQueryHelperTest` | `search_employee.feature` |
| `PayslipTab` | `PayslipFormatter` | `PayslipFormatterTest` | `payslip_formatting.feature` |
| `DeleteEmployeeTab` | `DeleteEmployeeHelper` | `DeleteEmployeeHelperTest` | – |
| `PayrollTabbedGUI` (search box) | `SearchFilterHelper` | `SearchFilterHelperTest` | – |

- **`AuthService.authenticate(username, password)`** – LoginTab-এর হার্ডকোড করা
  `"admin"/"admin123"` চেক এখন এখানে। Smoke: সঠিক credential দিয়ে true আসে।
  Sanity/Regression: ভুল username/password, case-mismatch, null – সব false।
- **`SearchQueryHelper`** – keyword সংখ্যা (ID search) না নাম (name search) তা
  নির্ধারণ এবং SQL/pattern তৈরি। Positive: numeric keyword -> ID SQL; name
  keyword -> name SQL + `%KEYWORD%` pattern (uppercase)। Negative: empty/null
  keyword -> non-numeric পথে যায়, pattern হয় `%%`।
- **`PayslipFormatter`** – পে-ডেট (`MM/dd/yyyy` -> `yyyy-MM-dd`) ও cheque-words
  ফরম্যাটিং (`"... dollars"` -> বাদ, `"... cents"` -> `/100`)। Positive: বৈধ
  তারিখ ও amount-in-words সঠিকভাবে রূপান্তরিত হয়। Negative: অবৈধ তারিখ
  (`"2025-10-11"`, `"13/01/2025"`, `"not-a-date"`) -> `DateTimeParseException`।

এই তিনটির জন্য রিগ্রেশন রক্ষা করা সহজ: ভবিষ্যতে UI পরিবর্তন হলেও এই ক্লাসগুলোর
টেস্ট অপরিবর্তিত থাকবে এবং কোনো লজিক ভেঙে গেলে ধরা পড়বে।

## ২. বাকি ক্লাসগুলো – কেন automate করা গেল না, এবং ম্যানুয়াল টেস্ট কেস

নিচের ক্লাসগুলো হয় শুধুই Swing লেআউট/নেভিগেশন কোড, অথবা সরাসরি `DBConnection`
(MySQL) এর উপর নির্ভরশীল এবং কোনো independent business logic নেই যা আলাদা করা
যায়। তাই এগুলোর জন্য ম্যানুয়াল (বা future UI-automation টুল যেমন
AssertJ-Swing/TestFX দিয়ে) smoke/sanity/regression checklist দেওয়া হলো।

### `DeleteEmployeeTab` — **validation/parsing এখন automated** (`DeleteEmployeeHelperTest.java`, plain JUnit5, headless-safe)
- **এখন automated (extracted)**: `DeleteEmployeeHelper.isBlank(idText)` এবং
  `DeleteEmployeeHelper.parseEmployeeId(idText)` আলাদা করা হয়েছে এবং
  `DeleteEmployeeTab.deleteEmployee()` এখন এগুলো ব্যবহার করে।
  - Positive: numeric ID (সহ leading/trailing space) সঠিকভাবে parse হয়।
  - Negative: `null`/empty/whitespace -> `isBlank` true; non-numeric
    (`"abc"`) বা decimal (`"12.5"`) -> `NumberFormatException`।
- **কোড পরিবর্তন (bug fix)**: আগে non-numeric ID দিলে `Integer.parseInt`
  থেকে `NumberFormatException` জেনেরিক `catch(Exception ex)`-এ গিয়ে
  "Error: For input string..." দেখাত। এখন `deleteEmployee()` আগে
  `DeleteEmployeeHelper.parseEmployeeId(...)` কল করে নির্দিষ্ট বার্তা
  "Employee ID must be a number" (লাল) দেখায় এবং DB কানেকশন খোলেই না।
- **Smoke (ম্যানুয়াল)**: Tab খোলে, "Employee ID" ফিল্ড ও "Delete Employee"
  বাটন দেখা যায়।
- **Sanity (positive, ম্যানুয়াল/DB)**: বিদ্যমান একটি Employee ID দিয়ে Delete
  করলে status "Employee deleted successfully!" (সবুজ) দেখায় এবং ডাটাবেস থেকে
  রো ডিলিট হয়।
- **Sanity/Regression (negative, ম্যানুয়াল/DB)**:
  - ফিল্ড খালি রেখে Delete -> "Please enter Employee ID" (লাল), কোনো DB কল হয় না।
  - non-numeric ID (e.g. "abc") -> "Employee ID must be a number" (লাল),
    DB কল হয় না (আগে generic "Error: ..." দেখাত — উপরে fix করা হয়েছে)।
  - অস্তিত্বহীন ID -> "Employee ID not found!" (লাল)।
  - DB connection বন্ধ থাকলে -> "Error: ..." (লাল), অ্যাপ ক্র্যাশ করে না।

### `ViewEmployeeTab` — **এখন automated** (`ViewEmployeeTabTest.java`, plain JUnit5, headless-safe)
- **Smoke (automated)**: `new ViewEmployeeTab(null)` exception ছোঁড়ে না এবং
  placeholder "Employee Data View" লেবেল প্যানেলে থাকে।
- **Sanity/Regression (negative, automated)**: `mainGui == null` হলে
  `reloadEmployees()` নিরাপদে কোনো কাজ না করে রিটার্ন করে (null-check
  ইতিমধ্যে আছে) – এক বা একাধিকবার কল করলেও exception ছোঁড়ে না।
- **Sanity (positive, এখনও ম্যানুয়াল/future)**: real `PayrollTabbedGUI`
  ইনস্ট্যান্স দিয়ে `reloadEmployees()` কল করলে `mainGui.loadEmployeesFromDB()`
  কল হয় – এর জন্য `PayrollTabbedGUI`-এর একটি constructable/mockable ভার্সন
  লাগবে (বর্তমানে DB-bound), তাই এই অংশ ম্যানুয়াল রইল।

### `WelcomeTab` — **এখন AssertJ-Swing দিয়ে automated** (`WelcomeTabTest.java`, `@Tag("ui")`)
- **Smoke**: ৯টি ক্লিকেবল প্যানেলের লেবেল (Login, Add Employee, ... , Exit)
  রেন্ডার হয়েছে কিনা যাচাই করা হয়।
- **Sanity (positive)**: Login / View Employees / Refresh প্যানেলে রোবট-ক্লিক
  করলে `tabbedPane`-এর সংশ্লিষ্ট ইনডেক্সে সুইচ হয় (`getSelectedIndex()` যাচাই)।
  হোভার-এ background/foreground রং বদলানো এখনও ম্যানুয়াল চেক (Robot দিয়ে hover
  করানো সম্ভব কিন্তু এই প্রথম পাসে অগ্রাধিকার পায়নি)।
- **Regression (negative)**: টেস্ট ফিক্সচারের `tabbedPane`-এ "Add Employee"
  নামের ট্যাব নেই, তাই সেই প্যানেলে ক্লিক করলে `indexOfTab` -> -1 হয়ে
  `setSelectedIndex` কল হয় না – `getSelectedIndex()` অপরিবর্তিত থাকে এবং কোনো
  exception ছুঁড়ে না, এটি assert করা হয়।
- **এড়ানো হয়েছে**: "Exit" প্যানেলে ক্লিক করা হয় না (শুধু লেবেল রেন্ডার চেক করা
  হয়), কারণ এতে `System.exit(0)` কল হয়ে test JVM বন্ধ হয়ে যাবে – এটি শুধুমাত্র
  ম্যানুয়ালি যাচাই করা হবে।
- **চালানোর শর্ত**: AssertJ-Swing `java.awt.Robot` ব্যবহার করে, তাই একটি
  on-screen ডিসপ্লে লাগবে (headless CI-তে চলবে না বা Xvfb লাগবে)।

### `RefreshTab` — **এখন AssertJ-Swing দিয়ে automated** (`RefreshTabTest.java`, `@Tag("ui")`)
- **Smoke (automated)**: "Refresh Employee Data" হেডার ও "🔄 Refresh Now"
  বাটন/status লেবেল রেন্ডার হয়েছে কিনা যাচাই করা হয়।
- **Sanity (positive, automated)**: `ViewEmployeeTab`/`UpdateEmployeeTab`-এর
  ট্র্যাকিং সাবক্লাস (যেহেতু Mockito নেই, override করে flag সেট করা হয়) দিয়ে
  বাটনে রোবট-ক্লিক করলে `reloadEmployees()` ও `clearFields()` উভয়ই কল হয়েছে
  এবং status "✅ Employee data refreshed successfully!" দেখায়, তা assert
  করা হয়।
- **Regression (negative, automated)**: `viewTab`/`updateTab` দুটোই `null`
  পাস করলে null-check থাকার কারণে কোনো কল হয় না কিন্তু exception ছোঁড়ে না এবং
  তবুও success message দেখায় (বিদ্যমান বিহেভিয়ার, automated test দিয়ে এখন লক
  করা আছে – ভবিষ্যতে পরিবর্তন করতে চাইলে এই টেস্টও আপডেট করতে হবে)।
- **এখনও ম্যানুয়াল**: `reloadEmployees()`/`clearFields()` থেকে real exception
  ছুঁড়লে catch ব্লক status-কে "❌ Error refreshing data: ..." (লাল) দেখাবে –
  এটি real DB ত্রুটির উপর নির্ভর করে তাই কভার করা হয়নি।
- **চালানোর শর্ত**: AssertJ-Swing `java.awt.Robot` ব্যবহার করে, তাই একটি
  on-screen ডিসপ্লে লাগবে (headless CI-তে চলবে না বা Xvfb লাগবে)।

### `LoginTab` (UI অংশ – `AuthService` ব্যতীত)
- **Smoke**: হেডার, username/password ফিল্ড, Login বাটন, ফুটার দেখা যায়।
- **Sanity (positive)**: সঠিক credential দিলে "Login successful!" ডায়ালগ এবং
  `tabbedPane.setSelectedIndex(0)` (Welcome ট্যাবে চলে যায়)।
- **Regression (negative)**: ভুল credential দিলে "Invalid username or
  password." (error ডায়ালগ), ট্যাব পরিবর্তন হয় না। (credential চেক নিজেই
  `AuthService`-এ ইউনিট-টেস্টেড।)

### `PayslipTab` (UI/DB অংশ – `PayslipFormatter` ব্যতীত)
- **Smoke**: Employee ID, Pay Date ইনপুট, "Generate Payslip & Cheque" বাটন,
  payslip ও cheque প্যানেল রেন্ডার হয়।
- **Sanity (positive)**: বৈধ Employee ID + বৈধ পে-ডেট (যার জন্য রেকর্ড আছে) ->
  payslip টেবিল ও cheque পূরণ হয়, সাকসেস ডায়ালগ দেখায়।
- **Regression (negative)**:
  - Employee ID/Pay Date খালি -> Bangla error ডায়ালগ "Employee ID এবং Pay Date
    অবশ্যই দিতে হবে!"
  - non-numeric Employee ID -> "Employee ID অবশ্যই একটি সঠিক সংখ্যা হতে হবে।"
  - ভুল ফরম্যাটের তারিখ -> "তারিখের বিন্যাস ভুল। MM/DD/YYYY বিন্যাস ব্যবহার
    করুন।" (এই দুটি ভ্যালিডেশন `PayslipFormatter`-এর মাধ্যমে ইতিমধ্যে
    ইউনিট-টেস্টেড)।
  - মিলে যায় এমন রেকর্ড না থাকলে -> payslip/cheque ক্লিয়ার হয় এবং "কোনো
    Payroll Record খুঁজে পাওয়া যায়নি" দেখায়।
  - DB error -> "Database Error: ..." ডায়ালগ, লগে SEVERE এন্ট্রি।

### `PayrollTabbedGUI`
- **Smoke**: অ্যাপ চালু হলে JFrame খোলে, সব ট্যাব (Welcome, Login, Add
  Employee, View Employees, Update Employee, Delete Employee, Search
  Employee, Payslip, Refresh) তৈরি হয়, sidebar বাটনগুলো দেখা যায়।
- **Sanity (positive)**:
  - `connectToDB()` সফল হলে কনসোলে "LOG: Connected to MySQL!" এবং
    `loadEmployeesFromDB()` টেবিল পপুলেট করে।
  - sidebar বাটনে ক্লিক করলে সঠিক ট্যাবে সুইচ হয়।
  - একটি এমপ্লয়ি যুক্ত হলে (`onEmployeeAdded`) View ট্যাব রিলোড হয়।
  - View টেবিলে row select করলে Update ট্যাবে ডেটা লোড হয়ে সেই ট্যাবে চলে যায়।
- **Regression (negative)**:
  - DB connection ব্যর্থ হলে `conn = null`, error ডায়ালগ দেখায়,
    `loadEmployeesFromDB()` চুপচাপ রিটার্ন করে (টেবিল খালি থাকে, ক্র্যাশ করে
    না)।
  - sidebar-এ "Exit" বাটনে ক্লিক করলে `System.exit(0)` (ম্যানুয়াল টেস্ট)।
- **বাগ ফিক্স (এখন automated, `SearchFilterHelperTest.java`)**: search
  filter-এ regex-injection জাতীয় ক্যারেক্টার (e.g. `"("`, `"*"`) দিলে আগে
  `RowFilter.regexFilter("(?i)" + text, 1, 2)` সরাসরি কল হত এবং
  `PatternSyntaxException` ছুঁড়ত (uncaught)। এখন এই লজিক
  `SearchFilterHelper.safeRegexFilter(text, columns...)`-এ আলাদা করা হয়েছে,
  যা invalid regex পেলে `null` রিটার্ন করে (filter বন্ধ, সব রো দেখায়) এবং
  `PayrollTabbedGUI`-এর `DocumentListener` এখন এটি ব্যবহার করছে। Positive:
  বৈধ regex (`"john"`) case-insensitive ম্যাচ করে; Negative: `"("`, `"*"`
  exception ছোঁড়ে না, `null` রিটার্ন করে।

### `UIHelper` — **এখন automated** (`UIHelperTest.java`, plain JUnit5, headless-safe)
- **Smoke/Sanity (positive)**: `UIHelper.setUIFont(new FontUIResource(...))`
  কল করার পর `UIManager`-এর সব `FontUIResource` এন্ট্রি নতুন ফন্টে বদলে গেছে
  কিনা যাচাই করা হয়।
- **Sanity**: দুইবার পরপর আলাদা ফন্ট দিয়ে কল করলে exception ছুঁড়ে না এবং
  শেষবারের ফন্টই সব এন্ট্রিতে থাকে (last-write-wins)।
- **Regression (negative, এখনও ম্যানুয়াল/ভবিষ্যতের কাজ)**: `null` পাস করলে
  `UIManager.put(key, null)` হবে এবং পরে `getFont()`-জাতীয় কল
  `NullPointerException` ছুঁড়তে পারে – বর্তমানে কোনো null-check নেই (সম্ভাব্য
  বাগ, কিন্তু production-এ সবসময় একটি বৈধ `FontUIResource` পাস করা হয়, তাই
  অগ্রাধিকার কম)।

## ৩. সারসংক্ষেপ

- নতুন pure-logic ক্লাস: `AuthService`, `SearchQueryHelper`,
  `PayslipFormatter`, `DeleteEmployeeHelper`, `SearchFilterHelper` – প্রতিটির
  জন্য JUnit5 (+ প্রথম তিনটির জন্য Cucumber) smoke/sanity/regression,
  positive+negative টেস্ট লেখা হয়েছে এবং `mvn clean verify` দিয়ে চলবে।
- `LoginTab`, `SearchEmployeeTab`, `PayslipTab`, `DeleteEmployeeTab`,
  `PayrollTabbedGUI` রিফ্যাক্টর করে এই extracted ক্লাসগুলো ব্যবহার করছে।
- **bug fix**: `PayrollTabbedGUI`-এর search filter এখন invalid regex
  (`"("`, `"*"`) দিলে `PatternSyntaxException` ছোঁড়ে না
  (`SearchFilterHelper.safeRegexFilter`)। `DeleteEmployeeTab`-এ non-numeric
  Employee ID দিলে এখন স্পষ্ট "Employee ID must be a number" বার্তা দেখায়
  (আগে generic "Error: For input string...")।
- `WelcomeTab`, `UIHelper`, `RefreshTab` AssertJ-Swing/plain JUnit5 দিয়ে
  automated (`WelcomeTabTest`, `UIHelperTest`, `RefreshTabTest`)।
  `ViewEmployeeTab`-এর null-mainGui কেস plain JUnit5 দিয়ে automated
  (`ViewEmployeeTabTest`)।
- বাকি যা ম্যানুয়াল: `PayrollTabbedGUI`-এর full DB-bound flow (connect, load,
  add/select row navigation, Exit বাটন), `PayslipTab`/`LoginTab`-এর UI অংশ,
  `DeleteEmployeeTab`/`ViewEmployeeTab`-এর real-DB sanity কেস, এবং
  `UIHelper.setUIFont(null)`-এর সম্ভাব্য NPE (নোট করা আছে, অগ্রাধিকার কম)।
