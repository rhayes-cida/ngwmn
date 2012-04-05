-- drop table CACHE_META_DATA;

create table CACHE_META_DATA (
	AGENCY_CD              VARCHAR2(20 BYTE)      NOT NULL,
    SITE_NO                VARCHAR2(16 BYTE)      NOT NULL,

    DATA_TYPE				VARCHAR2(24 BYTE)	DEFAULT 'ALL' NOT NULL,
    
    SUCCESS_CT				INTEGER,
    FAIL_CT					INTEGER,
    FIRST_DATA_DT			DATE,
    LAST_DATA_DT			DATE,
    MOST_RECENT_FETCH_DT	DATE,
    
	PRIMARY KEY (AGENCY_CD, SITE_NO, DATA_TYPE),
	FOREIGN KEY (AGENCY_CD, SITE_NO) 
	REFERENCES GW_DATA_PORTAL.WELL_REGISTRY (AGENCY_CD, SITE_NO) 
);