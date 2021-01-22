/*
 * Copyright  (c) 2020-2021 imlzw@vip.qq.com jweb.cc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.jweb.boot.utils.file.model;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * nio包下Files类删除文件夹所需辅助类
 * 
 * 
 * @author ag777
 * @version create on 2018年04月18日,last modify at 2018年04月24日
 */
public class DeleteDirectory  implements FileVisitor<Path> {
	private Path cur;
	private boolean skipOnErr;	//遇到删不掉的文件跳过
	
	public DeleteDirectory() {
		skipOnErr = false;
	}
	
	/**
	 * @param skipOnErr 遇到删不掉的文件是否跳过,true为跳过，默认是false
	 */
	public DeleteDirectory(boolean skipOnErr) {
		this.skipOnErr = skipOnErr;
	}
	
	public Path getCurPath() {
		return cur;
	}
	
	boolean deleteFileByFile(Path file) throws IOException {
		return Files.deleteIfExists(file);
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

		if (exc == null) {
			cur = dir;
			boolean success = deleteFileByFile((Path) dir);

			if (!success && !skipOnErr) {	//删除失败
				return FileVisitResult.TERMINATE;
			}
		} else {
			throw exc;
		}
		return FileVisitResult.CONTINUE;
	}

	/**
	 * 刚发现一个文件夹的时候(未去遍历子文件)
	 * <p>
	 * 由于只有文件夹非空时才能删除，所以请不要在这个方法里执行删除文件夹的操作
	 * 改为在postVisitDirectory()方法中删除文件夹
	 * </p>
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		cur = dir;
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		cur = file;
		boolean success = deleteFileByFile((Path) file);

		if (!success && !skipOnErr) {	//删除失败
			return FileVisitResult.TERMINATE;
		}

		return FileVisitResult.CONTINUE;
	}

	/**
	 * 如果文件不允许访问(删除不掉)的时候:
	 * FileVisitResult.TERMINATE 停止删除
	 * FileVisitResult.CONTINUE 跳过文件
	 */
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		cur = file;
		if(skipOnErr) {	//遇到异常时跳过该文件继续执行删除操作
			return FileVisitResult.CONTINUE;
		}
		return FileVisitResult.TERMINATE;
	}
}
