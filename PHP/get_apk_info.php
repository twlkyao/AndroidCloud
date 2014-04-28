<?php


	//$apk = "/opt/software/BaiduYun_5.4.0.apk";
	//$arr = readApkInfoFromFile($apk);
	//echo "label:" . $arr['lable'] . "<br>";
	//echo "sys_name" . $arr['icon'] . "<br>";
	
	/**
	 * @author Shiyao Qi
	 * @param $aapt_file The file path of the aapt file.
	 * @param $apk_file The file path of the apk file.
	 * @return The array of the information in AndroidManifest.xml file.
	 */
	  function readApkInfoFromFile($aapt_file, $apk_file, $get_icon = false){
		   exec("{$aapt_file} d badging {$apk_file}", $out, $return);
		   $temp_path = FCPATH . 'cache/temp/'.md5($apk_file).'/';
	 
		   if($return == 0){
				@mkdir($temp_path);
				$str_out = implode("\n", $out);
				$out = null;
	 
				#icon
				if($get_icon){
					$pattern_icon = "/icon='(.+)'/isU";
					preg_match($pattern_icon, $str_out, $m);
					$info['icon'] = $m[1];
					if($info['icon']){
						//$command = "unzip {$apk_file} {$info['icon']} -d " . $temp_path;
						 $command = '7z x "' . $apk_file . '" -y -aos -o"' . $temp_path . '" ' . $info['icon'];
						 //exit($command);
						//mkdirs("/tmp/".$info['icon'],true);
						exec($command);
						$info['icon'] = $temp_path . $info['icon'];
					}
				}
	 
				#对外显示名称
				$pattern_name = "/application: label='(.*)'/isU";
				preg_match($pattern_name, $str_out,$m);
				$info['lable']=$m[1];
	 
				#内部名称,软件唯一的
				$pattern_sys_name = "/package: name='(.*)'/isU";
				preg_match($pattern_sys_name, $str_out,$m);
				$info['sys_name']=$m[1];
	 
				#内部版本名称,用于检查升级
				$pattern_version_code = "/versionCode='(.*)'/isU";
				preg_match($pattern_version_code, $str_out,$m);
				$info['version_code']=$m[1];
	 
				#对外显示的版本名称
				$pattern_version = "/versionName='(.*)'/isU";
				preg_match($pattern_version, $str_out,$m);
				$info['version']=$m[1];
	 
				#系统
				$pattern_sdk = "/sdkVersion:'(.*)'/isU";
				if(preg_match($pattern_sdk, $str_out,$m)){
					$info['sdk_version']=$m[1];
					if($info['sdk_version']){
						$sdk_names = array(3=>"1.5",4=>"1.6",7=>"2.1",8=>"2.2",10=>'2.3.3',11=>"3.0",12=>"3.1",13=>"3.2",14=>"4.0");
						if($sdk_names[$info['sdk_version']]){
							$info['os_req'] = "Android {$sdk_names[$info['sdk_version']]}";
						}
					}
				}
	 
				 #权限
				$pattern_perm = "/uses-permission:'(.*)'/isU";
				preg_match_all($pattern_perm, $str_out,$m);
				if(isset($m[1])){
					foreach($m[1] as $mm){
						$info['permissions'][] = $mm;
					}
				}
	 
				#需要的功能(硬件支持)
				$pattern_features = "/uses-feature:'(.*)'/isU";
				preg_match_all($pattern_features, $str_out,$m);
				if(isset($m[1])){
					foreach($m[1] as $mm){
						$info['features'][] = $mm;
					}
				}
	 
				$info['apk_info'] = $str_out;
				return $info;
			}
			return false;
		}
?>
