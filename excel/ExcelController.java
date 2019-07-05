package cn.resource.jiaqi.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Strings;

import cn.platform.beans.PlatformCodeBean;
import cn.platform.beans.ResultTypeBean;
import cn.platform.beans.common.ResultBean;
import cn.platform.oa.common.action.AbstractBaseController;

@Controller
@RequestMapping(value = "/excel")
public class ExcelController extends AbstractBaseController {

	@GetMapping(value = "/export")
	public ResponseEntity<Object> exportUnbindPlatformCode(HttpServletRequest request, HttpServletResponse response) {
		try {
			List<PlatformCodeBean> list = new ArrayList<>();
			this.export(response, list);
			return new ResponseEntity<>(new ResultBean<>(true, "下载成功", "download_success", null), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ResultBean<>(false, ResultTypeBean.SERVERERROR_MSG, ResultTypeBean.SERVERERROR, null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void export(HttpServletResponse httpResponse, List<PlatformCodeBean> list) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("平台码列表");

		sheet.setColumnWidth(0, 17 * 256);
		sheet.setColumnWidth(1, 17 * 256);
		sheet.setColumnWidth(2, 24 * 256);
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 11);// 设置字体大小
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		ArrayList<String> cellList = new ArrayList<>();
		cellList.add("定位器ID");
		cellList.add("平台码");
		cellList.add("请在此列填写物理编号");
		HSSFCell cell;
		for (int i = 0; i < cellList.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(cellList.get(i));
			cell.setCellStyle(style);
		}
		for (int i = 0; i < list.size(); i++) {
			row = sheet.createRow(i + 1);
			// 定位器ID
			row.createCell(0).setCellValue(Strings.isNullOrEmpty(list.get(i).getImei()) ? "" : list.get(i).getImei().substring(1, list.get(i).getImei().length()));
			row.createCell(1).setCellValue(list.get(i).getPlatformCode());
		}
		String fileName = "平台码列表.xls";
		OutputStream os = httpResponse.getOutputStream();// 取得输出流
		httpResponse.reset();// 清空输出流
		httpResponse.setHeader("Content-disposition",
				"attachment; filename=" + new String(fileName.getBytes("GB2312"), "ISO8859-1"));
		httpResponse.setContentType("application/msexcel");
		wb.write(os);
		os.close();
	}

	/**
	 * 批量导入需要绑定商品的平台码信息
	 * 
	 * @param file
	 * @param platformCodeImportForm
	 * @param bindingResult
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/import")
	public ResponseEntity<Object> batchImport(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		if (null == file) {
			return new ResponseEntity<>(new ResultBean<>(false, "未找到上传文件", "no_file", null), HttpStatus.BAD_REQUEST);
		}
		String filename = file.getOriginalFilename();
		if (Strings.isNullOrEmpty(filename)) {
			return new ResponseEntity<>(new ResultBean<>(false, "请上传文件", "no_file", null), HttpStatus.BAD_REQUEST);
		}
		try {
			// 将Excel内容转为map
			ResultBean<List<Map<String, Object>>> result = this.getPlatformMap(file);
			if (!result.getIsSuccess()) {
				return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
			}
			ResultBean<Object> resultBean = new ResultBean<>();
			return new ResponseEntity<>(resultBean, resultBean.getIsSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>(
					new ResultBean<>(false, ResultTypeBean.SERVERERROR_MSG, ResultTypeBean.SERVERERROR, null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResultBean<List<Map<String, Object>>> getPlatformMap(MultipartFile file) {
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			Workbook workbook = WorkbookFactory.create(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			if (null == sheet) {
				return new ResultBean<>(false, "第一sheet未找到内容", "no_content", null);
			}
			if (sheet.getPhysicalNumberOfRows() <= 1) {
				return new ResultBean<>(false, "导入文件不能为空", "empty_file", null);
			}
			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row row = sheet.getRow(i);
				if (null == row) {
					continue;
				}
				Boolean flag1 = null == row.getCell(0) || Strings.isNullOrEmpty(row.getCell(0).toString().trim());
				Boolean flag2 = null == row.getCell(1) || Strings.isNullOrEmpty(row.getCell(1).toString().trim());
				Boolean flag3 = null == row.getCell(2) || Strings.isNullOrEmpty(row.getCell(2).toString().trim());
				if(flag1 && flag2 && flag3) {
					continue;
				}
				if(flag1 || flag2 || flag3) {
					return new ResultBean<>(false, "上传失败，请检查是否有为空的数据", "error", null); 
				}
				if(row.getCell(2).toString().trim().length() > 20) {
					return new ResultBean<>(false, "上传失败，请检查是否有物理编号长度超过20", "error", null); 
				}
				Map<String, Object> map = new HashMap<>();
				map.put("imei", "0" + row.getCell(0).toString());
				map.put("platformCode", row.getCell(1).toString());
				map.put("goodsNo", row.getCell(2).toString());
				list.add(map);
			}
			return new ResultBean<>(true, "转换成功", "success", list);
		} catch (Exception e) {
			return new ResultBean<>(false, "上传失败，请检查文件格式和内容是否符合要求", "error", null);
		}
	}
}