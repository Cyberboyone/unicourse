-- All University Lecture Notes — Complete Database Schema & Seed Data
-- 117 Nigerian University Standard Lecture Notes (CCMAS Baseline)

-- Tables
create table if not exists faculties (
    id          text primary key,
    name        text not null,
    cover_url   text
);

create table if not exists departments (
    id          text primary key,
    faculty_id  text not null references faculties(id) on delete cascade,
    name        text not null
);
create index if not exists departments_faculty_id_idx on departments(faculty_id);

create table if not exists courses (
    id                text primary key,
    department_id     text not null references departments(id) on delete cascade,
    title             text not null,
    cover_url         text not null,
    latest_version    integer not null default 1,
    file_size_bytes   bigint not null default 0,
    updated_at        timestamptz not null default now()
);
create index if not exists courses_department_id_idx on courses(department_id);
create index if not exists courses_title_idx on courses using gin (to_tsvector('english', title));

-- Row Level Security
alter table faculties enable row level security;
alter table departments enable row level security;
alter table courses enable row level security;

do $$ begin
    if not exists (select 1 from pg_policies where policyname = 'Public read access' and tablename = 'faculties') then
        create policy "Public read access" on faculties for select using (true);
    end if;
    if not exists (select 1 from pg_policies where policyname = 'Public read access' and tablename = 'departments') then
        create policy "Public read access" on departments for select using (true);
    end if;
    if not exists (select 1 from pg_policies where policyname = 'Public read access' and tablename = 'courses') then
        create policy "Public read access" on courses for select using (true);
    end if;
end $$;

-- 1. Seed Faculties (9 faculties)
insert into faculties (id, name) values
    ('science', 'Science'),
    ('management', 'Management'),
    ('arts', 'Arts'),
    ('education', 'Education'),
    ('social_science', 'Social Sciences'),
    ('agriculture', 'Agriculture'),
    ('law', 'Law'),
    ('health', 'Health Sciences'),
    ('general_studies', 'General Studies')
on conflict (id) do update set name = excluded.name;

-- 2. Seed Departments (25 departments)
insert into departments (id, faculty_id, name) values
    ('accounting', 'management', 'Accounting'),
    ('agric_econ', 'agriculture', 'Agricultural Economics'),
    ('agriculture_dept', 'agriculture', 'Agriculture'),
    ('biology', 'science', 'Biological Sciences'),
    ('business_admin', 'management', 'Business Administration'),
    ('chemistry', 'science', 'Chemistry'),
    ('computer_science', 'science', 'Computer Science'),
    ('economics', 'social_science', 'Economics'),
    ('education_dept', 'education', 'Education'),
    ('english', 'arts', 'English and Literary Studies'),
    ('entrepreneurship', 'general_studies', 'Entrepreneurship'),
    ('general_studies_dept', 'general_studies', 'General Studies'),
    ('history', 'arts', 'History and International Studies'),
    ('law_dept', 'law', 'Law'),
    ('marketing', 'management', 'Marketing'),
    ('math', 'science', 'Mathematics'),
    ('nursing', 'health', 'Nursing Science'),
    ('philosophy', 'arts', 'Philosophy'),
    ('physics', 'science', 'Physics'),
    ('political_science', 'social_science', 'Political Science'),
    ('psychology', 'social_science', 'Psychology'),
    ('public_admin', 'management', 'Public Administration'),
    ('public_health', 'health', 'Public Health'),
    ('sociology', 'social_science', 'Sociology'),
    ('stats', 'science', 'Statistics')
on conflict (id) do update set faculty_id = excluded.faculty_id, name = excluded.name;

-- 3. Seed Courses (117 courses)
insert into courses (id, department_id, title, cover_url, latest_version, file_size_bytes) values
    ('gst111', 'general_studies_dept', 'GST 111: Communication in English', 'covers/gst111.jpg', 2, 517351),
    ('gst112', 'general_studies_dept', 'GST 112: Nigerian Peoples and Culture', 'covers/gst112.jpg', 2, 801107),
    ('mth101', 'math', 'MTH 101: Elementary Mathematics I', 'covers/mth101.jpg', 2, 558321),
    ('mth102', 'math', 'MTH 102: Elementary Mathematics II', 'covers/mth102.jpg', 2, 715018),
    ('phy101', 'physics', 'PHY 101: General Physics I — Mechanics', 'covers/phy101.jpg', 2, 768480),
    ('bio101', 'biology', 'BIO 101: General Biology I', 'covers/bio101.jpg', 2, 693858),
    ('chm101', 'chemistry', 'CHM 101: General Chemistry I', 'covers/chm101.jpg', 2, 649613),
    ('eco101', 'economics', 'ECO 101: Principles of Economics I', 'covers/eco101.jpg', 2, 690650),
    ('sta111', 'stats', 'STA 111: Descriptive Statistics', 'covers/sta111.jpg', 2, 600463),
    ('phy102', 'physics', 'PHY 102: General Physics II — Electricity, Magnetism and Modern Physics', 'covers/phy102.jpg', 2, 530526),
    ('chm102', 'chemistry', 'CHM 102: General Chemistry II', 'covers/chm102.jpg', 2, 534862),
    ('bio102', 'biology', 'BIO 102: General Biology II', 'covers/bio102.jpg', 2, 521810),
    ('ent101', 'entrepreneurship', 'ENT 101: Introduction to Entrepreneurship', 'covers/ent101.jpg', 2, 539133),
    ('csc101', 'computer_science', 'CSC 101: Introduction to Computer Science', 'covers/csc101.jpg', 1, 540815),
    ('eco102', 'economics', 'ECO 102: Principles of Economics II', 'covers/eco102.jpg', 1, 580995),
    ('acc101', 'accounting', 'ACC 101: Introduction to Financial Accounting', 'covers/acc101.jpg', 1, 552449),
    ('pos101', 'political_science', 'POS 101: Introduction to Political Science and Nigerian Government', 'covers/pos101.jpg', 1, 544584),
    ('soc101', 'sociology', 'SOC 101: Introduction to Sociology', 'covers/soc101.jpg', 1, 496676),
    ('psy101', 'psychology', 'PSY 101: Introduction to Psychology', 'covers/psy101.jpg', 1, 467210),
    ('csc102', 'computer_science', 'CSC 102: Introduction to Computer Programming', 'covers/csc102.jpg', 1, 449391),
    ('gst121', 'general_studies_dept', 'GST 121: Use of Library, Study Skills and ICT', 'covers/gst121.jpg', 1, 471132),
    ('acc102', 'accounting', 'ACC 102: Introduction to Financial Accounting II', 'covers/acc102.jpg', 1, 491588),
    ('bus101', 'business_admin', 'BUS 101: Introduction to Business', 'covers/bus101.jpg', 1, 518832),
    ('bus102', 'business_admin', 'BUS 102: Introduction to Management', 'covers/bus102.jpg', 1, 433467),
    ('law101', 'law_dept', 'LAW 101: Nigerian Legal System and Legal Method', 'covers/law101.jpg', 1, 478579),
    ('mkt101', 'marketing', 'MKT 101: Principles of Marketing', 'covers/mkt101.jpg', 1, 478249),
    ('pad101', 'public_admin', 'PAD 101: Introduction to Public Administration', 'covers/pad101.jpg', 1, 460180),
    ('bus103', 'business_admin', 'BUS 103: Business Mathematics', 'covers/bus103.jpg', 1, 458576),
    ('agr101', 'agriculture_dept', 'AGR 101: Introduction to Agriculture', 'covers/agr101.jpg', 1, 460003),
    ('eng101', 'english', 'ENG 101: Introduction to Literature', 'covers/eng101.jpg', 1, 458129),
    ('phl101', 'philosophy', 'PHL 101: Introduction to Philosophy', 'covers/phl101.jpg', 1, 471142),
    ('his101', 'history', 'HIS 101: Introduction to Nigerian History', 'covers/his101.jpg', 1, 482281),
    ('edu101', 'education_dept', 'EDU 101: Introduction to Education', 'covers/edu101.jpg', 1, 442503),
    ('law102', 'law_dept', 'LAW 102: Nigerian Legal System', 'covers/law102.jpg', 1, 487772),
    ('phs101', 'public_health', 'PHS 101: Introduction to Public Health', 'covers/phs101.jpg', 1, 454579),
    ('nur101', 'nursing', 'NUR 101: Fundamentals of Nursing', 'covers/nur101.jpg', 1, 462904),
    ('acc201', 'accounting', 'ACC 201: Cost Accounting', 'covers/acc201.jpg', 1, 486107),
    ('law201', 'law_dept', 'LAW 201: Law of Contract I', 'covers/law201.jpg', 1, 470853),
    ('phs201', 'public_health', 'PHS 201: Health Promotion and Education', 'covers/phs201.jpg', 1, 436167),
    ('edu201', 'education_dept', 'EDU 201: Educational Psychology', 'covers/edu201.jpg', 1, 459371),
    ('agr201', 'agriculture_dept', 'AGR 201: Principles of Crop Production', 'covers/agr201.jpg', 1, 445623),
    ('csc201', 'computer_science', 'CSC 201: Computer Programming I', 'covers/csc201.jpg', 1, 471220),
    ('eco201', 'economics', 'ECO 201: Principles of Microeconomics I', 'covers/eco201.jpg', 1, 547878),
    ('bus201', 'business_admin', 'BUS 201: Principles of Management I', 'covers/bus201.jpg', 1, 505617),
    ('eng201', 'english', 'ENG 201: An Introduction to Morphology and Syntax', 'covers/eng201.jpg', 1, 467590),
    ('agr202', 'agriculture_dept', 'AGR 202: Principles of Animal Production', 'covers/agr202.jpg', 1, 513925),
    ('edu202', 'education_dept', 'EDU 202: Curriculum and Instruction', 'covers/edu202.jpg', 1, 490885),
    ('gst202', 'general_studies_dept', 'GST 202: Peace Studies and Conflict Resolution', 'covers/gst202.jpg', 1, 481386),
    ('nur201', 'nursing', 'NUR 201: Foundation of Nursing III', 'covers/nur201.jpg', 1, 488342),
    ('pos201', 'political_science', 'POS 201: Nigerian Government and Politics I', 'covers/pos201.jpg', 1, 569037),
    ('his201', 'history', 'HIS 201: History of Nigeria up to 1800', 'covers/his201.jpg', 1, 495001),
    ('law202', 'law_dept', 'LAW 202: Law of Contract II', 'covers/law202.jpg', 1, 494614),
    ('pad201', 'public_admin', 'PAD 201: Introduction to Public Administration', 'covers/pad201.jpg', 1, 527523),
    ('mkt201', 'marketing', 'MKT 201: Elements of Marketing I', 'covers/mkt201.jpg', 1, 510949),
    ('soc201', 'sociology', 'SOC 201: History of Social Thought', 'covers/soc201.jpg', 1, 501506),
    ('eco202', 'economics', 'ECO 202: Principles of Economics II', 'covers/eco202.jpg', 1, 535182),
    ('law303', 'law_dept', 'LAW 303: Law of Torts I', 'covers/law303.jpg', 1, 500067),
    ('csc301', 'computer_science', 'CSC 301: Data Structures', 'covers/csc301.jpg', 1, 504714),
    ('edu301', 'education_dept', 'EDU 301: Educational Administration and Planning', 'covers/edu301.jpg', 1, 505715),
    ('aec301', 'agric_econ', 'AEC 301: Farm Management and Accounting', 'covers/aec301.jpg', 1, 529623),
    ('phs301', 'public_health', 'PHS 301: Health Management Information Systems', 'covers/phs301.jpg', 1, 504538),
    ('chm301', 'chemistry', 'CHM 301: Physical Chemistry III', 'covers/chm301.jpg', 1, 527509),
    ('phy301', 'physics', 'PHY 301: Classical Mechanics', 'covers/phy301.jpg', 1, 494703),
    ('bio301', 'biology', 'BIO 301: Genetics II', 'covers/bio301.jpg', 1, 503163),
    ('mth301', 'math', 'MTH 301: Abstract Algebra I', 'covers/mth301.jpg', 1, 465433),
    ('agr302', 'agriculture_dept', 'AGR 302: Agricultural Statistics and Data Processing', 'covers/agr302.jpg', 1, 487996),
    ('edu305', 'education_dept', 'EDU 305: Educational Technology', 'covers/edu305.jpg', 1, 484041),
    ('his202', 'history', 'HIS 202: Economic History of Nigeria in the 19th Century', 'covers/his202.jpg', 1, 444433),
    ('eco301', 'economics', 'ECO 301: Microeconomic Theory I', 'covers/eco301.jpg', 1, 394440),
    ('eng202', 'english', 'ENG 202: Introduction to Poetry and Drama', 'covers/eng202.jpg', 1, 455592),
    ('gst201', 'general_studies_dept', 'GST 201: Nigerian Peoples and Culture', 'covers/gst201.jpg', 1, 431391),
    ('law203', 'law_dept', 'LAW 203: Constitutional Law I', 'covers/law203.jpg', 1, 410098),
    ('nur301', 'nursing', 'NUR 301: Mental Health Nursing I', 'covers/nur301.jpg', 1, 378967),
    ('phl201', 'philosophy', 'PHL 201: Introduction to Metaphysics', 'covers/phl201.jpg', 1, 444916),
    ('law204', 'law_dept', 'LAW 204: Constitutional Law II', 'covers/law204.jpg', 1, 402633),
    ('phs305', 'public_health', 'PHS 305: Seminars in Public Health', 'covers/phs305.jpg', 1, 388475),
    ('edu302', 'education_dept', 'EDU 302: ICT in Education', 'covers/edu302.jpg', 1, 386533),
    ('gst101', 'general_studies_dept', 'GST 101: Use of English and Communication Skills I', 'covers/gst101.jpg', 1, 390309),
    ('gst102', 'general_studies_dept', 'GST 102: Use of English and Communication Skills II', 'covers/gst102.jpg', 1, 375767),
    ('bio201', 'biology', 'BIO 201: Cell Biology', 'covers/bio201.jpg', 1, 365738),
    ('chm201', 'chemistry', 'CHM 201: Organic Chemistry I', 'covers/chm201.jpg', 1, 364544),
    ('mkt301', 'marketing', 'MKT 301: Principles of Marketing', 'covers/mkt301.jpg', 1, 383154),
    ('acc301', 'accounting', 'ACC 301: Financial Accounting', 'covers/acc301.jpg', 1, 377877),
    ('eng301', 'english', 'ENG 301: Discourse Analysis and English Usage', 'covers/eng301.jpg', 1, 372916),
    ('his301', 'history', 'HIS 301: Nigerian History', 'covers/his301.jpg', 1, 376279),
    ('acc401', 'accounting', 'ACC 401: Advanced Financial Accounting', 'covers/acc401.jpg', 1, 380387),
    ('bus401', 'business_admin', 'BUS 401: Business Policy and Strategy', 'covers/bus401.jpg', 1, 421701),
    ('eng401', 'english', 'ENG 401: Stylistics and Literary Language', 'covers/eng401.jpg', 1, 373357),
    ('his401', 'history', 'HIS 401: Historiography and Nigerian History', 'covers/his401.jpg', 1, 376536),
    ('bio401', 'biology', 'BIO 401: Population Genetics', 'covers/bio401.jpg', 1, 365709),
    ('chm401', 'chemistry', 'CHM 401: Instrumental Methods of Analysis', 'covers/chm401.jpg', 1, 367304),
    ('csc401', 'computer_science', 'CSC 401: Software Engineering', 'covers/csc401.jpg', 1, 365445),
    ('law401', 'law_dept', 'LAW 401: Law of Evidence', 'covers/law401.jpg', 1, 384015),
    ('edu401', 'education_dept', 'EDU 401: Research Methods and Data Processing', 'covers/edu401.jpg', 1, 411514),
    ('nur401', 'nursing', 'NUR 401: Medical-Surgical Nursing III', 'covers/nur401.jpg', 1, 421745),
    ('agr401', 'agriculture_dept', 'AGR 401: Agricultural Product Storage', 'covers/agr401.jpg', 1, 409434),
    ('phs401', 'public_health', 'PHS 401: Health Policy and Finance', 'covers/phs401.jpg', 1, 424452),
    ('eco401', 'economics', 'ECO 401: Advanced Microeconomics I', 'covers/eco401.jpg', 1, 425206),
    ('pos401', 'political_science', 'POS 401: Development Administration', 'covers/pos401.jpg', 1, 416209),
    ('phl401', 'philosophy', 'PHL 401: Late Modern Philosophy', 'covers/phl401.jpg', 1, 406410),
    ('gst401', 'general_studies_dept', 'GST 401: Citizenship, Ethics and National Development', 'covers/gst401.jpg', 1, 407622),
    ('phy201', 'physics', 'PHY 201: Classical Mechanics I', 'covers/phy201.jpg', 1, 398057),
    ('mth201', 'math', 'MTH 201: Mathematical Methods I', 'covers/mth201.jpg', 1, 393451),
    ('soc301', 'sociology', 'SOC 301: Sociological Theory and Analysis', 'covers/soc301.jpg', 1, 412215),
    ('pos301', 'political_science', 'POS 301: Political Thoughts I', 'covers/pos301.jpg', 1, 405771),
    ('mth401', 'math', 'MTH 401: Theory of Ordinary Differential Equations', 'covers/mth401.jpg', 1, 433116),
    ('phy401', 'physics', 'PHY 401: Quantum Mechanics I', 'covers/phy401.jpg', 1, 416611),
    ('phl301', 'philosophy', 'PHL 301: Symbolic Logic', 'covers/phl301.jpg', 1, 414401),
    ('psy201', 'psychology', 'PSY 201: Determinants of Behaviour', 'covers/psy201.jpg', 1, 420905),
    ('mth202', 'math', 'MTH 202: Elementary Differential Equations', 'covers/mth202.jpg', 1, 402852),
    ('phy202', 'physics', 'PHY 202: Electric Circuits and Electronics', 'covers/phy202.jpg', 1, 404253),
    ('chm202', 'chemistry', 'CHM 202: Inorganic Chemistry II', 'covers/chm202.jpg', 1, 416407),
    ('bio202', 'biology', 'BIO 202: General Physiology', 'covers/bio202.jpg', 1, 406600),
    ('bus301', 'business_admin', 'BUS 301: Management Theory I', 'covers/bus301.jpg', 1, 427437),
    ('mkt401', 'marketing', 'MKT 401: Marketing Management', 'covers/mkt401.jpg', 1, 428654),
    ('psy301', 'psychology', 'PSY 301: Clinical Psychology', 'covers/psy301.jpg', 1, 433592),
    ('soc401', 'sociology', 'SOC 401: Contemporary Sociological Theories', 'covers/soc401.jpg', 1, 420168)
on conflict (id) do update set department_id = excluded.department_id, title = excluded.title, cover_url = excluded.cover_url, latest_version = excluded.latest_version, file_size_bytes = excluded.file_size_bytes;
