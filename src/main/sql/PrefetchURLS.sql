-- urls is like http://localhost:8080/ngwmn/data?agency_cd=NJGS&featureID=2288614
-- FIPS codes:
-- Montana 30
-- Minnesota 27

select '<a href="http://localhost:8080/ngwdp/data?agency_cd=' || agency_cd || '&featureID=' || site_no ||'">' || agency_cd || ':' || site_no || '</href>' url from well_registry
where state_cd = 30;
