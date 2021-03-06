package groovy.controller

import com.nh.micro.rule.engine.core.GInputParam;
import com.nh.micro.rule.engine.core.GOutputParam;
import com.nh.micro.rule.engine.core.GContextParam;
import com.nh.micro.rule.engine.core.GroovyExecUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import java.sql.PreparedStatement;
import groovy.json.*;
import groovy.template.MicroMvcTemplate;

import com.nh.micro.db.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.nh.micro.cache.base.*;
import com.nh.micro.db.Cutil;


class MatchMvcSaleList extends MicroMvcTemplate{
	public String tableName="match_sale";
	public String getTableName(HttpServletRequest httpRequest){
		return tableName;
	}
	public void execMatch(GInputParam gInputParam,GOutputParam gOutputParam,GContextParam gContextParam){
		HttpServletRequest httpRequest = gContextParam.getContextMap().get("httpRequest");
		Map reportMap=new HashMap();
		String rules=httpRequest.getParameter("rules");
		//String rules="rule_product";
		String recordId=httpRequest.getParameter("recordId");
		String dirFlag="sale2buy";


		String matchId=UUID.randomUUID().toString();
		String[] ruleArray=rules.split(",");
		List ruleList=new ArrayList();

		if(rules.equals("all")){
			List<Map> queryList=getInfoListAllService(new HashMap(),"match_rule",new HashMap());
			for(Map row:queryList){
				ruleList.add(row.get("rule_id"));
			}
		}else{
			ruleList=ruleArray.toList();
		}
		
		//GroovyExecUtil.execGroovyRetObj("mem_match","matchRule", ruleList, matchId, recordId, dirFlag);
		Map contextMap=new HashMap();
		contextMap.put("msg", "");
		boolean status=GroovyExecUtil.execGroovyRetObj("mem_match","matchRule",ruleList,matchId,recordId,dirFlag,contextMap);
		gOutputParam.setResultMsg(contextMap.get("msg"));
		if(status==false){
			gOutputParam.setResultStatus(1);
		}else{
			Map updateMap=new HashMap();
			updateMap.put("match_time", "now()");
			updateInfoByIdService(recordId,"match_sale",updateMap);
		}

		return;
	}
}
