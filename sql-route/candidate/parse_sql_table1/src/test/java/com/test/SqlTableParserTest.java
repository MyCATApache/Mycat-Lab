package com.test;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class SqlTableParserTest extends TestCase {

    @Test
    public void testParse1() throws Exception {
        List<String> tables;
        try {
            SqlTableParser parser = new SqlTableParser(sql5);
            tables = parser.getTables();
        } catch (Exception e) {
            String error = e.getMessage();
        }

        int a = 1;
    }

    private String sql1 = "(select a from t1) union (select b from t2)";

    private String sql2 = "select t3.*,ztd3.TypeDetailName as UseStateName\n" +
            "from\n" +
            "( \n" +
            " select t4.*,ztd4.TypeDetailName as AssistantUnitName\n" +
            " from\n" +
            " (\n" +
            "  select t2.*,ztd2.TypeDetailName as UnitName \n" +
            "  from\n" +
            "  (\n" +
            "   select t1.*,ztd1.TypeDetailName as MaterielAttributeName \n" +
            "   from \n" +
            "   (\n" +
            "    select m.*,r.RoutingName,u.username,mc.MoldClassName\n" +
            "    from dbo.D_Materiel as m\n" +
            "    left join dbo.D_Routing as r\n" +
            "    on m.RoutingID=r.RoutingID\n" +
            "    left join dbo.D_MoldClass as mc\n" +
            "    on m.MoldClassID=mc.MoldClassID\n" +
            "    left join dbo.D_User as u\n" +
            "    on u.UserId=m.AddUserID\n" +
            "   )as t1\n" +
            "   left join dbo.D_Type_Detail as ztd1 \n" +
            "   on t1.MaterielAttributeID=ztd1.TypeDetailID\n" +
            "  )as t2\n" +
            "  left join dbo.D_Type_Detail as ztd2 \n" +
            "  on t2.UnitID=ztd2.TypeDetailID\n" +
            " ) as t4\n" +
            " left join dbo.D_Type_Detail as ztd4 \n" +
            " on t4.AssistantUnitID=ztd4.TypeDetailID\n" +
            ")as t3\n" +
            "left join dbo.D_Type_Detail as ztd3 \n" +
            "on t3.UseState=ztd3.TypeDetailID";

    private static final String sql3 = "(select a from t1) union (select b from t2)";


    private static final String sql4 = "Select d.Fabric_No,\n" +
            "       f.MachineName,\n" +
            "       f.RawNo,\n" +
            "       f.OldRawNo,\n" +
            "       f.RawName,\n" +
            "       f.StructCode,\n" +
            "       p.WorkClass,\n" +
            "       d.DefectType,\n" +
            "       d.DefectName,\n" +
            "       f.InspectResult,\n" +
            "       Convert(Char(10), InspectDate, 20) As InspectDate,\n" +
            "       (Case\n" +
            "         When f.StructCode = 'JT' Then\n" +
            "          Convert(Decimal(28, 2),\n" +
            "                  (d.DefectEnd - d.DefectStart + 1) /\n" +
            "                  dbo.f_JT_CalcMinValue(LPair, RPair) * Allow_Qty)\n" +
            "         Else\n" +
            "          (d.DefectEnd - d.DefectStart + 1)\n" +
            "       End) As MLength,\n" +
            "       (Case\n" +
            "         When f.StructCode = 'JT' Then\n" +
            "          ISNULL((Select SUM(DefectEnd)\n" +
            "                   From FIInspectFabricDefects s\n" +
            "                  Where DefectStart >= d.DefectStart\n" +
            "                    And DefectStart <= d.DefectEnd\n" +
            "                    And Fabric_No = d.Fabric_No\n" +
            "                    And RecType = '疵点'),\n" +
            "                 0.00)\n" +
            "         Else\n" +
            "          ISNULL((Select SUM(DefectEnd - DefectStart + 1)\n" +
            "                   From FIInspectFabricDefects s\n" +
            "                  Where DefectStart >= d.DefectStart\n" +
            "                    And DefectStart <= d.DefectEnd\n" +
            "                    And DefectEnd >= d.DefectStart\n" +
            "                    And DefectEnd <= d.DefectEnd\n" +
            "                    And Fabric_No = d.Fabric_No\n" +
            "                    And RecType = '疵点'),\n" +
            "                 0.00)\n" +
            "       End) As DefectNum,\n" +
            "       Convert(Decimal(28, 2),\n" +
            "               (d.DefectEnd - d.DefectStart + 1.0) / (Case\n" +
            "                 When f.StructCode = 'JT' Then\n" +
            "                  dbo.f_JT_CalcMinValue(LPair, RPair)\n" +
            "                 Else\n" +
            "                  f.Allow_Qty\n" +
            "               End) * f.Allow_Wt) As MWeight,\n" +
            "       (Case\n" +
            "         When f.StructCode = 'JT' Then\n" +
            "          (Select B.DefectName\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk * DefectEnd) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 1)\n" +
            "         Else\n" +
            "          (Select B.DefectName\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk *\n" +
            "                                      (DefectEnd - DefectStart + 1)) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                              And s.DefectEnd >= d.DefectStart\n" +
            "                              And s.DefectEnd <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 1)\n" +
            "       End) As OneDefectName,\n" +
            "       (Case\n" +
            "         When f.StructCode = 'JT' Then\n" +
            "          (Select B.DefectNum\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk * DefectEnd) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 1)\n" +
            "         Else\n" +
            "          (Select B.DefectNum\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk *\n" +
            "                                      (DefectEnd - DefectStart + 1)) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                              And s.DefectEnd >= d.DefectStart\n" +
            "                              And s.DefectEnd <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 1)\n" +
            "       End) As OneDefect,\n" +
            "       (Case\n" +
            "         When f.StructCode = 'JT' Then\n" +
            "          (Select B.DefectName\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk * DefectEnd) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 2)\n" +
            "         Else\n" +
            "          (Select B.DefectName\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk *\n" +
            "                                      (DefectEnd - DefectStart + 1)) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                              And s.DefectEnd >= d.DefectStart\n" +
            "                              And s.DefectEnd <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 2)\n" +
            "       End) As TwoDefectName,\n" +
            "       (Case\n" +
            "         When f.StructCode = 'JT' Then\n" +
            "          (Select B.DefectNum\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk * DefectEnd) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 2)\n" +
            "         Else\n" +
            "          (Select B.DefectNum\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk *\n" +
            "                                      (DefectEnd - DefectStart + 1)) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                              And s.DefectEnd >= d.DefectStart\n" +
            "                              And s.DefectEnd <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 2)\n" +
            "       End) As TwoDefect,\n" +
            "       (Case\n" +
            "         When f.StructCode = 'JT' Then\n" +
            "          (Select B.DefectName\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk * DefectEnd) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 3)\n" +
            "         Else\n" +
            "          (Select B.DefectName\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk *\n" +
            "                                      (DefectEnd - DefectStart + 1)) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                              And s.DefectEnd >= d.DefectStart\n" +
            "                              And s.DefectEnd <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 3)\n" +
            "       End) As ThreeDefectName,\n" +
            "       (Case\n" +
            "         When f.StructCode = 'JT' Then\n" +
            "          (Select B.DefectNum\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk * DefectEnd) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 3)\n" +
            "         Else\n" +
            "          (Select B.DefectNum\n" +
            "             From (Select A.*,\n" +
            "                          ROW_NUMBER() Over(Order By DefectNum Desc) As RecNo\n" +
            "                     From (Select SUM(DefectBulk *\n" +
            "                                      (DefectEnd - DefectStart + 1)) As DefectNum,\n" +
            "                                  DefectName,\n" +
            "                                  DefectType\n" +
            "                             From FIInspectFabricDefects s\n" +
            "                            Where s.RecType = '疵点'\n" +
            "                              And s.Fabric_No = d.Fabric_No\n" +
            "                              And s.DefectStart >= d.DefectStart\n" +
            "                              And s.DefectStart <= d.DefectEnd\n" +
            "                              And s.DefectEnd >= d.DefectStart\n" +
            "                              And s.DefectEnd <= d.DefectEnd\n" +
            "                            Group By DefectType, DefectName) A) B\n" +
            "            Where B.RecNo = 3)\n" +
            "       End) As ThreeDefect\n" +
            "  From FIInspectFabric f, FIInspectFabricDefects d, t_ORC_StanPersonSet P\n" +
            " Where d.RecType = '产量'\n" +
            "   And d.DefectType = p.PNo\n" +
            "   And f.Fabric_NO = d.Fabric_No\n" +
            " Order By d.Fabric_No, p.WorkClas\n";

    private static final String sql5 = "select p, s.count as views, (select count(*) from Comments rc where rc.linkedId=p.id and rc.classcode='InfoPublishs') as commentNumber, (select count(*) from CollectIndexs rci where rci.toId=p.id and rci.classcode='InfoPublishs' and rci.type='favorite') as favorite FROM InfoPublishs p,UserScores s where p.id=s.linkedId and p.userInfo.id=s.userInfo.id and s.classCode='InfoPublishs' AND p.status=?1 ORDER BY p.createtime DESC";

}