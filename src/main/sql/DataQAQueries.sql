select qc.quality_cache_id, xq.*
from quality_cache qc,
XMLTable('//time' passing qc.xml
columns "t" varchar(40) path '.') as xq;


-- namespace http://qwwebservices.usgs.gov/schemas/WQX-Outbound/2_0/
select qc.quality_cache_id,xq.*
from quality_cache qc,
XMLTable(
XMLNAMESPACES(default 'http://qwwebservices.usgs.gov/schemas/WQX-Outbound/2_0/'),
'//CharacteristicName'
passing qc.xml
columns "cn" varchar(40) path '.'
) as xq;

-- how to splat your namespace
select
qc.quality_cache_id
from quality_cache qc
where
XMLExists('//*:CharacteristicName'
passing qc.xml
);

-- so the empty cache ids are:
select
qc.quality_cache_id
from quality_cache qc
where
not XMLExists('//*:CharacteristicName'
passing qc.xml
);
