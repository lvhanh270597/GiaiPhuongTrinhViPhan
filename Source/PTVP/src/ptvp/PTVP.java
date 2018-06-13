
package ptvp;

import com.maplesoft.externalcall.MapleException;
import com.maplesoft.openmaple.Engine;
import com.maplesoft.openmaple.EngineCallBacksDefault;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.List;
import javafx.util.Pair;

public class PTVP {

    public static Engine engine;
    
    public static void connectToMaple() throws MapleException{
       
        
    }       
    
    public static String solve(String PTVP) throws MapleException{
         String mapleArgs[];        
        mapleArgs = new String[1];
        mapleArgs[0] = "java";
        engine = new Engine( mapleArgs, new EngineCallBacksDefault(), null, null );	   
        
        // build functions in here
        engine.evaluate("#----------------------------------  ------------------------------------\n" +
"chuyenVePt := proc (fString)\n" +
"	local ep; \n" +
"	ep := parse(fString);\n" +
"	ep := lhs(ep) - rhs(ep) = 0;\n" +
"	return convert(ep, string);\n" +
"end proc:\n" +
"\n" +
"taoChuoi := proc(str, L)\n" +
"	uses StringTools;\n" +
"	local new_str, n, i, temp;\n" +
"	new_str := str;\n" +
"	n := numelems(L);\n" +
"	for i from 1 to n do\n" +
"		temp := cat(\"{\", convert(i, string), \"}\");\n" +
"		new_str := Substitute(new_str, temp, L[i]);\n" +
"	end do;\n" +
"	return new_str;\n" +
"end proc:\n" +
"\n" +
"getSetOfChar := proc()\n" +
"	local charExpr, s;\n" +
"	uses StringTools;\n" +
"	charExpr := \"()*+-_./0123456789=ABCDEFGHIJKLMNOPQRSTUVWXYZ^abcdefghijklmnopqrstuvwxyz\";	\n" +
"	s := {seq(charExpr[i], i=1..length(charExpr))};\n" +
"	return s;\n" +
"end proc:\n" +
"tien_xu_ly := proc(str)	\n" +
"	local setOfChars, new_str, c;\n" +
"	uses StringTools;	\n" +
"	# xóa kí tự thừa\n" +
"	setOfChars := getSetOfChar();\n" +
"	new_str := \"\";\n" +
"	for c in str do\n" +
"		if (c = \"'\") or (c in setOfChars) then\n" +
"			new_str := cat(new_str, c);\n" +
"		end if;\n" +
"	end do;\n" +
"	# thay thế ' bằng _	\n" +
"	new_str := Substitute(new_str, \"'\", \"_\");\n" +
"	# thêm dấu bằng		\n" +
"	if Search(\"=\", new_str) = 0 then\n" +
"		new_str := cat(new_str, \"=0\");\n" +
"	else \n" +
"		new_str := chuyenVePt(new_str);\n" +
"	end if;			\n" +
"	return new_str;\n" +
"end proc:\n" +
"\n" +
"getLeft := proc(fString)\n" +
"	local f;\n" +
"	f := parse(fString);\n" +
"	return convert(lhs(f), string);\n" +
"end proc:\n" +
"\n" +
"getLatex := proc(f)\n" +
"	return cat(\"\\\\(\", latex(f, output=string), \"\\\\)\"); \n" +
"end proc:\n" +
"\n" +
"#---------------------------------------------------------------------------------\n" +
"#---------------------------------- NHẬN DẠNG ------------------------------------\n" +
"\n" +
"# ví dụ: xy = yu + xs thì input là: xy - yu - xs\n" +
"# Ví dụ: y' = y / x + e^(y / x)  thì input là: y' - y / x - e^(y / x)\n" +
"\n" +
"check2 := proc(f, var1, var2)\n" +
"	local vars, g;\n" +
"	g := subs(var1 = 1, f);\n" +
"	g := subs(var2 = 21, g);\n" +
"	g := subs(e = 0, g);\n" +
"	vars := indets(g);		\n" +
"	return evalb(numelems(vars) = 0);\n" +
"end proc:\n" +
"\n" +
"check := proc(f, var)\n" +
"	local vars, g;\n" +
"	g := subs(var = 0.1653001562, f);\n" +
"	g := subs(e = 0.1653001562, g);\n" +
"	vars := indets(g);		\n" +
"	return evalb(numelems(vars) = 0);\n" +
"end proc:\n" +
"\n" +
"check_tach_bien := proc (fString) \n" +
"	local fx, gy, f; \n" +
"	uses StringTools;	\n" +
"	if evalb(0 < Search(\"y_\", fString)) then \n" +
"		return [false];\n" +
"	end if; \n" +
"	f := parse(fString); \n" +
"	fx := coeff(f, dx, 1); \n" +
"	gy := coeff(f, dy, 1); \n" +
"	if check(fx, x) and check(gy, y) then \n" +
"		return [true, fx, gy];\n" +
"	else \n" +
"		return [false] \n" +
"	end if; \n" +
"end proc:\n" +
"\n" +
"check_dang_cap := proc(fString)	\n" +
"	local g, gString, f;	\n" +
"	uses StringTools;\n" +
"	if evalb(0 = Search(\"y_\", fString)) then \n" +
"		return [false];\n" +
"	end if; \n" +
"	gString := Subs(\"y_\" = \"0\", fString):\n" +
"	g := parse(gString):\n" +
"	g := algsubs(y/x = u, g):\n" +
"	if check(g, u) then\n" +
"		return [true, parse(gString)];\n" +
"	end if;\n" +
"	return [false];\n" +
"end proc:\n" +
"\n" +
"check_tuyen_tinh_cap_1 := proc(fString)\n" +
"	local g, gString, px, qx;\n" +
"	uses StringTools;\n" +
"	try		\n" +
"		if evalb(0 = Search(\"y_\", fString)) then \n" +
"			return [false];\n" +
"		end if; \n" +
"		gString := Subs(\"y_\" = \"0\", fString):\n" +
"		g := parse(gString):		\n" +
"		px := coeff(g, y, 1);		\n" +
"		qx := expand(px * y) - expand(g);		\n" +
"		if check(px, x) and check(qx, x) then\n" +
"			return [true, px, qx];\n" +
"		end if;\n" +
"	catch:\n" +

"		return [false];\n" +
"	end try;\n" +
"	return [false];\n" +
"end proc:\n" +
"\n" +
"check_bernouli := proc(fString)\n" +
"	local f, g, beta, alpha, remind, gString, i, px, qx;\n" +
"	uses StringTools;\n" +
"	try						\n" +
"		if Search(\"y_\", fString) = 0 then \n" +
"			return [false];\n" +
"		end if; \n" +
"		# Xóa y'\n" +
"		gString := Subs(\"y_\" = \"0\", fString):\n" +
"		g := parse(gString);\n" +
"		f := expand(g / y);	\n" +
"		\n" +
"		remind := expand(g) - expand(f * y);\n" +
"		if remind != 0 then\n" +
"			return [false];\n" +
"		end if;		\n" +
"		# tim alpha		\n" +
"		px := coeff(f, y, 0);\n" +
"		f := expand(f) - expand(px);		\n" +
"		g := coeffs(f, y);				\n" +
"		if numelems({g}) != 1 then\n" +
"			return [false];\n" +
"		end if;\n" +
"		alpha := -10;		\n" +
"		for beta from -5 to 5 do\n" +
"			if expand(g * (y ^ beta)) = expand(f) then\n" +
"				alpha := beta + 1;\n" +
"				break;\n" +
"			end if;\n" +
"		end do;	\n" +
"		if alpha = -10 then \n" +
"			return [false];\n" +
"		end if;\n" +
"		qx := -g;				\n" +
"		if check(px, x) and check(qx, x) then\n" +
"			return [true, px, qx, alpha];\n" +
"		end if;\n" +
"	catch:\n" +
"		return [false];\n" +
"	end try;\n" +
"	return [false];	\n" +
"end proc:\n" +
"\n" +
"check_vi_phan_toan_phan := proc(fString)\n" +
"	local pxy, qxy, f;\n" +
"	f := parse(fString); \n" +
"	pxy := coeff(f, dx, 1);\n" +
"	qxy := coeff(f, dy, 1);	\n" +
"	if check2(pxy, x, y) and check2(qxy, x, y) then\n" +
"		return [true, pxy, qxy];\n" +
"	else\n" +
"		return [false];\n" +
"	end if;\n" +
"end proc:\n" +
"\n" +
"nhan_dien := proc(gString)\n" +
"	local f, fString;\n" +
"	fString := tien_xu_ly(gString);			\n" +
"	fString := getLeft(fString);\n" +
"	f := check_tach_bien(fString);\n" +
"	if f[1] = true then\n" +
"		return [\"tach_bien\", f[2], f[3]];\n" +
"	end if;\n" +
"	f := check_dang_cap(fString);\n" +
"	if f[1] = true then\n" +
"		return [\"dang_cap\", f[2]];\n" +
"	end if;\n" +
"	f := check_tuyen_tinh_cap_1(fString);\n" +
"	if f[1] = true then\n" +
"		return [\"tuyen_tinh\", f[2], f[3]];\n" +
"	end if;\n" +
"	f := check_bernouli(fString);\n" +
"	if f[1] = true then\n" +
"		return [\"bernouli\", f[2], f[3], f[4]];\n" +
"	end if;	\n" +
"	f := check_vi_phan_toan_phan(fString);\n" +
"	if f[1] = true then\n" +
"		return [\"toan_phan\", f[2], f[3]];\n" +
"	end if;\n" +
"	return [\"unknown\"];\n" +
"end proc:\n" +
"\n" +
"#---------------------------------------------------------------------------------\n" +
"#----------------------------------  ------------------------------------\n" +
"giaiPtTachBien:=proc(L)\n" +
"	local	fxPf, gyPf, raw, resEx, Fx, Gy, \n" +
"			rawPf, int_expr_Pf, resPf;\n" +
"	local _res;\n" +
"	\n" +
"	Fx := L[1];  Gy := L[2];\n" +
"	_res := \"Phuong trinh cua ban la phuong trinh tach bien \\n \";\n" +
"	fxPf := getLatex(Fx);\n" +
"	gyPf := getLatex(Gy);\n" +
"	_res := cat(_res, \"Ta dua phuong trinh ve dang:  \\n \");\n" +
"	raw := Fx*dx + Gy*dy;\n" +
"	rawPf := getLatex(raw, output=string);\n" +
"	_res := cat(_res, taoChuoi(\"{1} = 0\", [rawPf]));\n" +
"	_res := cat(_res, \" \\n Tiep theo ta tinh nguyen ham 2 ve: \\n \");\n" +
"	int_expr_Pf := getLatex(Int(Fx, x) + Int(Gy, y));\n" +
"	_res := cat(_res, taoChuoi(\"{1} = 0 \", [int_expr_Pf]));\n" +
"	_res := cat(_res, \"  \\n Vay, nghiem tong quat cua phuong trinh la:  \\n \");\n" +
"	resEx := int(Fx,x) + int(Gy,y) = C;\n" +
"	resPf := getLatex(resEx);\n" +
"	_res := cat(_res, resPf);\n" +
"	return _res;\n" +
"end proc:\n" +
"\n" +
"dang_cap := proc(L)\n" +
"	local FxPf, fuPf, fu, u, resTB, Fx;\n" +
"	local resPf;\n" +
"	local _res;\n" +
"	\n" +
"	Fx := -L[1];		\n" +
"	_res := \"Phuong trinh cua ban la phuong trinh dang cap \\n \";\n" +
"	FxPf := getLatex(Fx);	\n" +
"	_res := cat(_res, taoChuoi(\"Phuong trinh cua ban la: y' = {1}  \\n \", [FxPf]));\n" +
"	_res := cat(_res, \"Cac buoc giai:  \\n \");\n" +
"	_res := cat(_res, \"Dat u = y/x => y = u.x => y' = u + xu' \\n \");\n" +
"	_res := cat(_res, \"Thay y' vao phuong trinh dau ta duoc:  \\n \");\n" +
"\n" +
"	fu := algsubs(y / x = u, Fx) - u;\n" +
"	fuPf := getLatex(fu);\n" +
"	_res := cat(_res, taoChuoi(\"xu' = {1}  \\n \", [fuPf]));\n" +
"	_res := cat(_res, \"Day la phuong trinh tach bien, ta se giai dua theo phuong trinh tach bien \\n \");\n" +
"	fu := algsubs(u = y, fu);		\n" +
"	resTB := giaiPtTachBien([-1/x, 1/fu]);\n" +
"	_res := cat(_res, \"Giai phuong trinh tach bien tren ta duoc  \\n \");\n" +
"	\n" +
"	resTB := algsubs(y = u, resTB);\n" +
"	resPf := getLatex(getTB);\n" +
"	_res := cat(_res, taoChuoi(\"{1}  \\n \", [resPf]));\n" +
"	\n" +
"	resTB := algsubs(u = y / x, resTB);\n" +
"	resPf := getLatex(resTB);	\n" +
"	_res := cat(_res, taoChuoi(\"Tuong duong voi: {1}  \\n \", [resPf]));\n" +
"	return _res;\n" +
"end proc:\n" +
"\n" +
"tuyen_tinh_cap_1 := proc(L) \n" +
"	local PxPf, QxPf, aPf, yPf, y, res1, res2, a, C, Px, Qx;\n" +
"	local _res;\n" +
"	\n" +
"	Px := L[1];\n" +
"	Qx := L[2];\n" +
"	_res := \"Phuong trinh cua ban la phuong trinh tuyen tinh cap 1 \\n \";\n" +
"	\n" +
"	PxPf := getLatex(Px);\n" +
"	QxPf := getLatex(Qx);\n" +
"	_res := cat(_res, taoChuoi(\"Phuong trinh cua ban la: y' + ({1})y = {2}  \\n \", [PxPf, QxPf]));\n" +
"	\n" +
"	_res := cat(_res, \"Cac buoc giai:  \\n \");\n" +
"	_res := cat(_res, taoChuoi(\"Dau tien, minh tinh nguyen ham cua  {1},  \\n Dat no la a. Ta co:  \\n \", [PxPf]));\n" +
"	a := int(Px, x);\n" +
"	aPf := getLatex(Int(Px, x));\n" +
"	_res := cat(_res, taoChuoi(\"<=> a = {1}  \\n \", [aPf]));\n" +
"	yPf := getLatex(exp(-a) * (Int(Qx * exp(a), x)) + C);\n" +
"	_res := cat(_res, taoChuoi(\"Vay ket qua la: y = {1}  \\n \", [yPf]));\n" +
"	y := exp(-1 * a) * (int(Qx * exp(a), x) + C);\n" +
"	res1 := getLatex(y);\n" +
"	_res := cat(_res, taoChuoi(\"=> y = {1}  \\n \", [res1]));\n" +
"	res2 := getLatex(expand(y));\n" +
"	if length(res1) > length(res2) then\n" +
"		_res := cat(_res, taoChuoi(\"<=> y = {1}\", [res2]));\n" +
"	end if;\n" +
"	return [y, _res];\n" +
"end proc:\n" +
"\n" +
"bernouli := proc(L)\n" +
"	local PxPf, QxPf, aPf, z, zPf, diff_z, diff_y, y;\n" +
"	local main_expr,diff_z_before, expr_ps, _Px, _Qx, Px, Qx, alpha;\n" +
"	local exprPf, daoHamZY, daoHamZYPf, yPf;\n" +
"	local _res; \n" +
"	\n" +
"	Px := L[1];\n" +
"	Qx := L[2];\n" +
"	alpha := L[3];\n" +
"\n" +
"	_res := \"Phuong trinh cua ban la phuong trinh bernouli \\n \";\n" +
"	PxPf := getLatex(Px);\n" +
"	QxPf := getLatex(Qx);\n" +
"	aPf := getLatex(alpha);\n" +
"	\n" +
"	main_expr := diff_y + Px * y = Qx * (y ^ alpha);\n" +
"	exprPf := getLatex(Px * y = Qx * y ^ alpha);\n" +
"	\n" +
"	_res := cat(_res, taoChuoi(\"Phuong trinh cua ban la: y' + {1} (1) \\n \", [exprPf]));\n" +
"	_res := cat(_res, \"Cac buoc giai:  \\n \");\n" +
"	z := y ^ (1 - alpha);\n" +
"	zPf := getLatex(z);	\n" +
"	_res := cat(_res, taoChuoi(\"Dat z = {1}  \\n \", [zPf]));\n" +
"	daoHamZY := diff(z, y);\n" +
"	daoHamZYPf := getLatex(Diff(z, y));	\n" +
"	_res := cat(_res, taoChuoi(\"Suy ra, z' = ({1})y' \\n \", [daoHamZYPf]));\n" +
"	_res := cat(_res, taoChuoi(\"Nhan ca 2 ve cua phuong trinh (1) voi {1}, ta co:  \\n \", [daoHamZYPf]));\n" +
"	main_expr := expand(main_expr * daoHamZY);\n" +
"	exprPf := getLatex(main_expr);\n" +
"	_res := cat(_res, taoChuoi(\"(1) <=> {1}  \\n \", [exprPf]));\n" +
"	\n" +
"	diff_z_before := _z * diff_y;		\n" +
"	main_expr := algsubs(diff_z_before = _z, main_expr);	\n" +
"	z := 'z';\n" +
"	main_expr := algsubs(y ^ (1 - alpha) = z, main_expr);\n" +
"\n" +
"	exprPf := getLatex(main_expr);\n" +
"	_res := cat(_res, taoChuoi(\"==> {1}\", [exprPf]));\n" +
"	_res := cat(_res, \"(Day la phuong trinh tuyen tinh cap 1) \\n \");\n" +
"	_Px := coeff(lhs(main_expr), z);\n" +
"	_Qx := rhs(main_expr);\n" +
"	\n" +
"	z := tuyen_tinh_cap_1([_Px, _Qx]);\n" +
"	_res := cat(_res, z[2]);\n" +
"	z := z[1];\n" +
"	zPf := getLatex(z);	\n" +
"	_res := cat(_res, taoChuoi(\" \\n z = {1}\", [zPf]));\n" +
"	\n" +
"	yPf := getLatex(y^(1-alpha) = z);\n" +
"	_res := cat(_res, taoChuoi(\" \\n <=> {1}\", [yPf]));\n" +
"end proc:\n" +
"\n" +
"giai_ptviphan_toanphan := proc(L) \n" +
"	local pxyPf, qxyPf, diff_Pxy, diff_Qxy, int_Py, int_Qx;\n" +
"	local res, resPf, Pxy, Qxy, nguyenHamPxy, Diff_Uy;\n" +
"	local daoHam_nguyenHamPxy_theo_y, daoHam_c_y, c_y, int_Px;\n" +
"	local daoHamPYPf1, daoHamPY, daoHamPYPf2 , daoHamQXPf1, daoHamQX, daoHamQXPf2;\n" +
"	local nguyenHamPXPf1, nguyenHamPX, nguyenHamPXPf2, daoHamUYPf;\n" +
"	local daoHam_nguyenHamPxy_theo_yPf, daoHam_c_yPf, c_yPf;\n" +
"	local _res;\n" +
"	\n" +
"	uses StringTools;\n" +
"	Pxy := L[1];\n" +
"	Qxy := L[2];\n" +
"	\n" +
"	_res := \"Phuong trinh cua ban la phuong trinh vi phan toan phan \\n \";\n" +
"	pxyPf := getLatex(Pxy);\n" +
"	qxyPf := getLatex(Qxy);\n" +
"	_res := cat(_res, taoChuoi(\"Phuong trinh co dang: {1}dx + {2}dy = 0  \\n \", [pxyPf, qxyPf]));\n" +
"	daoHamPYPf1 := getLatex(Diff(Pxy, y));\n" +
"	_res := cat(_res, taoChuoi(\"Cach giai: \\n Tinh {1}  \\n \", [daoHamPYPf1]));\n" +
"	daoHamPY := diff(Pxy, y); \n" +
"	daoHamPYPf2 := getLatex(daoHamPY);	\n" +
"	_res := cat(_res, taoChuoi(\"{1} = {2} (1) \\n \", [daoHamPYPf1, daoHamPYPf2]));\n" +
"	\n" +
"	daoHamQXPf1 := getLatex(Diff(Qxy, x));\n" +
"	_res := cat(_res, taoChuoi(\"Tinh {1}  \\n \", [daoHamQXPf1]));\n" +
"	daoHamQX := diff(Qxy, x); \n" +
"	daoHamQXPf2 := getLatex(daoHamQX);	\n" +
"	_res := cat(_res, taoChuoi(\"Q'x = ({1}) (2) \\n \", [daoHamQXPf2]));\n" +
"	\n" +
"	# kiem tra 2 cai giong nhau\n" +
"	daoHamPY := sort(daoHamPY);\n" +
"	daoHamQX := sort(daoHamQX);\n" +
"	if daoHamPY = daoHamQX then \n" +
"		_res := cat(_res, taoChuoi(\"{1} = {2} = {3}  \\n \", [daoHamPYPf1, daoHamQXPf1, daoHamPYPf2]));\n" +
"		nguyenHamPXPf1 := getLatex(Int(Pxy, x));\n" +
"		_res := cat(_res, taoChuoi(\"Tu (1) ta suy ra: U(x, y) = {1} + c(y) \\n \", [nguyenHamPXPf1]));\n" +
"		nguyenHamPX := int(Pxy, x);\n" +
"		nguyenHamPXPf2 := getLatex(nguyenHamPX);\n" +
"		_res := cat(_res, taoChuoi(\"=> U(x, y) = {1} + c(y) \\n \", [nguyenHamPXPf2]));\n" +
"		daoHamUYPf := getLatex(Diff(U, y));\n" +
"		daoHam_nguyenHamPxy_theo_y := diff(nguyenHamPX, y);		\n" +
"		daoHam_nguyenHamPxy_theo_yPf := getLatex(Diff(nguyenHamPX, y));\n" +
"		_res := cat(_res, taoChuoi(\"=> {1} = {2} + c'(y) = {3}  \\n \", [daoHamUYPf, daoHam_nguyenHamPxy_theo_yPf, qxyPf]));\n" +
"		daoHam_c_y := Qxy - daoHam_nguyenHamPxy_theo_y;\n" +
"		daoHam_c_yPf := getLatex(daoHam_c_y);		\n" +
"		_res := cat(_res, taoChuoi(\"==> c'(y) = {1}  \\n \", [daoHam_c_yPf]));\n" +
"		c_y := int(daoHam_c_y, y);\n" +
"		c_yPf := getLatex(Int(daoHam_c_y, y));\n" +
"		_res := cat(_res, taoChuoi(\"==> c(y) = {1} + c1 \\n \", [c_yPf]));\n" +
"			\n" +
"		res := nguyenHamPX + c_y;\n" +
"		res := subs(ln(e)=1, res);\n" +
"		resPf := getLatex(res);\n" +
"		_res := cat(_res, taoChuoi(\"Vay, ket qua la: U(x, y) = {1} + c1 \\n \", [resPf]));\n" +
"	else\n" +
"		_res := cat(_res, \"Chuong trinh khong co kha nang giai phuong trinh nay.\");\n" +
"	end if;\n" +
"	return _res;\n" +
"end proc:\n" +
"\n" +
"#----------------------------------------------------------------------------------\n" +
"#----------------------------------------MAIN--------------------------------------\n" +
"\n" +
"giai_phuong_trinh_vi_phan := proc(fString)\n" +
"	local argus, kind, methods;\n" +
"	local _res;\n" +
"	methods := table([\"tach_bien\" = giaiPtTachBien, \"dang_cap\" = dang_cap, \n" +
"	\"bernouli\" = bernouli, \"tuyen_tinh\" = tuyen_tinh_cap_1, \"toan_phan\" = giai_ptviphan_toanphan]);			\n" +
"	argus := nhan_dien(fString);	\n" +
"	kind := argus[1];\n" +
"	_res := \"\";\n" +
"	if kind = \"unknown\" then \n" +
"		_res := \"Chuong trinh khong co kha nang giai phuong trinh nay \\n \";\n" +
"		return ;\n" +
"	end if;	\n" +
"	_res := cat(_res, methods[kind](argus[2..]));\n" +
"	return _res;\n" +
"end proc:\n" +
"\n" );
        PTVP = "\"" + PTVP + "\"";
        System.out.println("giai_phuong_trinh_vi_phan(" + PTVP + ");");
        String res = engine.evaluate("giai_phuong_trinh_vi_phan(" + PTVP + ");").toString();
        return res;
    }
     public static Pair<List<String>, List<Integer>> tach(String st){
        List<String> listOfSentences = new Vector<String>();
        List<Integer> listOfBool = new Vector<Integer>();
        int Left = st.indexOf("\\(");
        int Right = st.indexOf("\\)");
        if (Left > -1 && Right > -1){                                
            String str = st.substring(0, Left);                
            listOfSentences.add(str);

            str = st.substring(Left, Right + 2);                
            listOfSentences.add(str);

            str = st.substring(Right + 2);                
            listOfSentences.add(str);             

            listOfBool.add(0);
            listOfBool.add(1);
            listOfBool.add(0);
        }            
        else{
            listOfSentences.add(st);
            listOfBool.add(0);
        }
        return new Pair<List<String>, List<Integer>> (listOfSentences, listOfBool);
    }
    public static void exeCmd() throws IOException{
        String ex1 = "MyTex";
        String ex2 = "config.xml";
        
        ProcessBuilder builder = new ProcessBuilder(
                "MikTex.exe", ex1, ex2);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                System.out.println(line);
            }
    }
    
    public static List<String> split(String myString){
        myString = myString.substring(1, myString.length() - 1);
        List<String> res = new Vector<String>();
        int begin = 0;
        for (int i=0; i<myString.length() - 1; i++){
            if (myString.charAt(i) == '\\'){
                if ((myString.charAt(i + 1) == 'n')){
                    res.add(myString.substring(begin, i));
                    begin = i + 2;
                }
            } 
        }
        res.add(myString.substring(begin, myString.length()));   
        List<String> _res = new Vector<String>();
        for (String st : res){
            System.out.println("res" + st);
            begin = 0;
            String str = "";
            for (int i=0; i<st.length() - 1; i++){
                if (st.charAt(i) == '\\'){
                    if (st.charAt(i + 1) == '\\'){
                        str += st.substring(begin, i);
                        begin = i + 1;
                    }
                }                    
            }            
            str += st.substring(begin, st.length());
            System.out.println("str" + str);
            _res.add(str);
        }
        return _res;
    }
    
    public static void PDFReader(){
         try {
		File pdfFile = new File("MyTex.pdf");
		if (pdfFile.exists()) {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(pdfFile);
			} else {
				System.out.println("Awt Desktop is not supported!");
			}
		} else {
			System.out.println("File is not exists!");
		}

		System.out.println("Done");

	  } catch (Exception ex) {
		ex.printStackTrace();
	  }        
    }
    
    public static void writeToFile(String myString) throws IOException{
        String fileName = "MyTex.tex";
        String begin = "\\documentclass{article}" + "\n" + "\\begin{document}" + "\n";
        String end = "\\end{document}";
        
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        
        printWriter.println(begin);                
        
        List<String> lines = split(myString);
        System.out.println(lines);
        for (String line : lines){
            //line =  line ;
            line = line + "\\newline";
            printWriter.println(line);
            System.out.println(line);
        }
        
        printWriter.println(end);
        
        printWriter.close();
    }
    public static void main(String[] args) throws MapleException {        
        mainInterface inter = new mainInterface();
        inter.show();       
    }
    
}
