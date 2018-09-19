 SELECT distinct vnf_name, pserver_fqdn, pserver_hostname
            from qa0_dcaecommon_views.v_vnf_vm_pserver;
            
SELECT distinct vnf_name, pserver_fqdn, pserver_hostname from qa0_dcaecommon_views.v_vce_vm_pserver where vnf_name in ('ZRDM1MMSC04')
UNION
SELECT distinct vnf_name, pserver_fqdn, pserver_hostname from qa0_dcaecommon_views.v_vnf_vm_pserver where vnf_name in ('ZRDM1MMSC04')
UNION
SELECT distinct vnf_name, pserver_fqdn, pserver_hostname from qa0_dcaecommon_views.v_vpe_vm_pserver where vnf_name in ('ZRDM1MMSC04')
;