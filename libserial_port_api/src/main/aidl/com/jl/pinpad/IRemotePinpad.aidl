package com.jl.pinpad;

interface IRemotePinpad{
	/**
		初始化pinpad
	*/
	void init();
	
	/**
	  获取版本号
	*/
	String getVer();
	
	/**
		输入密码，联机PIN
		@param timeOutS: 密码输入超时时间秒为单位
		@param amount: 交易金额 分为单位
		@param lenSet: 接受输入的PIN长度集合如"0,4,6,12"表示允许输入长度为0 4 6 12的PIN
		@param keyId: 用于计算pinblock的工作密钥索引
		@param pan: 卡号，如果参与计算的pinblock算法中需要主账号此参数为主账号，否则为空
		@param alg: 算法指示器 国际标准算法ANSIX9_8(0) 中国银联算法CUP_PB(1)
		@param pinBlock: 最终的输出结果pinBlock
		@param finishMode: 结束方式 0-确认结束 1-长度满足结束

		@return 0 成功，其它失败。
				PED_ERR_INVALID_PARAM（-10000）参数非法
				PCIERR_INVALID_ARGUMENTS = -2000,  参数非法
				PCIERR_INVALID_TMKINDEX = -2001,非法密钥索引
				PCIERR_SAVE_TMK_FAILED = -2002,	保存密钥失败
				PCIERR_INVALID_PWK_INDEX = -2003,	非法密钥索引
				PCIERR_SAVE_PWK_FAILED = -2004,	 保存密钥失败
				PCIERR_READ_TMK_FAILED = -2005,  读取密钥失败
				PCIERR_USER_CANCEL		 = -2006, 用户取消
				PCIERR_USER_SKIP		 = -2007, 用户bypass
				PCIERR_READ_PWK_FAILED 	= -2008,  读取密钥失败
				PCIERR_READ_MWK_FAILED	= -2009, 读密钥失败
				PCIERR_READ_DWK_FAILED	= -2010, 读密钥失败
				PCIERR_READ_KEK_FAILED = -2011, 读密钥失败
	*/
	int InputPin(int timeOutS, int amount, String lenSet, int keyId, String pan, int alg, out byte[]pinBlock, int type, int finishMode);
	
	String InputPlainTextPin(int timeOutS, int amount, String lenSet, int finishMode);
	
	
	/**
		计算MAC
		@param msg  将要进行计算MAC的消息
		@param keyId 密钥索引
		@param alg 算法指示 
						XORMAC(0), 
						ANSIX9_9(1),
						ANSIX9_19(2),	
						CUP_MAC(3),
		@param mac 最终输出的报文甄别吗
		@return 0 成功其它失败。
				PED_ERR_INVALID_PARAM（-10000）参数非法
				PCIERR_INVALID_ARGUMENTS = -2000,  参数非法
				PCIERR_INVALID_TMKINDEX = -2001,非法密钥索引
				PCIERR_SAVE_TMK_FAILED = -2002,	保存密钥失败
				PCIERR_INVALID_PWK_INDEX = -2003,	非法密钥索引
				PCIERR_SAVE_PWK_FAILED = -2004,	 保存密钥失败
				PCIERR_READ_TMK_FAILED = -2005,  读取密钥失败
				PCIERR_USER_CANCEL		 = -2006, 用户取消
				PCIERR_USER_SKIP		 = -2007, 用户bypass
				PCIERR_READ_PWK_FAILED 	= -2008,  读取密钥失败
				PCIERR_READ_MWK_FAILED	= -2009, 读密钥失败
				PCIERR_READ_DWK_FAILED	= -2010, 读密钥失败
				PCIERR_READ_KEK_FAILED = -2011, 读密钥失败
	*/
	int CalculateMac(in byte[] msg, int keyId, int alg, out byte[] mac, in byte[] iv,int type);
	
	/**
		用指定索引的密钥进行加密
		@param plainText 明文数据
		@param keyId 密钥索引
		@param encryptText 输出的密文数据
		@return 0 成功其它失败。
				PED_ERR_INVALID_PARAM（-10000）参数非法
				PCIERR_INVALID_ARGUMENTS = -2000,  参数非法
				PCIERR_INVALID_TMKINDEX = -2001,非法密钥索引
				PCIERR_SAVE_TMK_FAILED = -2002,	保存密钥失败
				PCIERR_INVALID_PWK_INDEX = -2003,	非法密钥索引
				PCIERR_SAVE_PWK_FAILED = -2004,	 保存密钥失败
				PCIERR_READ_TMK_FAILED = -2005,  读取密钥失败
				PCIERR_USER_CANCEL		 = -2006, 用户取消
				PCIERR_USER_SKIP		 = -2007, 用户bypass
				PCIERR_READ_PWK_FAILED 	= -2008,  读取密钥失败
				PCIERR_READ_MWK_FAILED	= -2009, 读密钥失败
				PCIERR_READ_DWK_FAILED	= -2010, 读密钥失败
				PCIERR_READ_KEK_FAILED = -2011, 读密钥失败
	*/
	int desEncrypt(in byte[] plainText, int keyId, out byte[] encryptText, in byte[] iv, int type);
	/**
		用指定索引的密钥进行解密
		@param plainText 输出明文数据
		@param keyId 密钥索引
		@param encryptText 密文数据
		@return 0 成功其它失败。
				PED_ERR_INVALID_PARAM（-10000）参数非法
				PCIERR_INVALID_ARGUMENTS = -2000,  参数非法
				PCIERR_INVALID_TMKINDEX = -2001,非法密钥索引
				PCIERR_SAVE_TMK_FAILED = -2002,	保存密钥失败
				PCIERR_INVALID_PWK_INDEX = -2003,	非法密钥索引
				PCIERR_SAVE_PWK_FAILED = -2004,	 保存密钥失败
				PCIERR_READ_TMK_FAILED = -2005,  读取密钥失败
				PCIERR_USER_CANCEL		 = -2006, 用户取消
				PCIERR_USER_SKIP		 = -2007, 用户bypass
				PCIERR_READ_PWK_FAILED 	= -2008,  读取密钥失败
				PCIERR_READ_MWK_FAILED	= -2009, 读密钥失败
				PCIERR_READ_DWK_FAILED	= -2010, 读密钥失败
				PCIERR_READ_KEK_FAILED = -2011, 读密钥失败
	*/
	int desDecrypt(out byte[] plainText , int keyId, in byte[] encryptText, in byte[] iv, int type);
	/**
		存储终端主密钥
		@param key 主密钥明文或者密文
		@param keyId 密钥索引
		@param dependKeyId 依赖密钥索引当TMK为密文输入时用于解密得到明文TMK
		@param cv 密钥校验值
		@param type 密钥类型 01 des密钥 02 SM密钥
		@return 0 成功其它失败。
				PED_ERR_INVALID_PARAM（-10000）参数非法
				PCIERR_INVALID_ARGUMENTS = -2000,  参数非法
				PCIERR_INVALID_TMKINDEX = -2001,非法密钥索引
				PCIERR_SAVE_TMK_FAILED = -2002,	保存密钥失败
				PCIERR_INVALID_PWK_INDEX = -2003,	非法密钥索引
				PCIERR_SAVE_PWK_FAILED = -2004,	 保存密钥失败
				PCIERR_READ_TMK_FAILED = -2005,  读取密钥失败
				PCIERR_USER_CANCEL		 = -2006, 用户取消
				PCIERR_USER_SKIP		 = -2007, 用户bypass
				PCIERR_READ_PWK_FAILED 	= -2008,  读取密钥失败
				PCIERR_READ_MWK_FAILED	= -2009, 读密钥失败
				PCIERR_READ_DWK_FAILED	= -2010, 读密钥失败
				PCIERR_READ_KEK_FAILED = -2011, 读密钥失败
	*/
	int storeTmk(in byte[] key, int keyId, int dependKeyId, in byte[] cv, int type);
	/**
		存储PIN工作密钥
		@param key PIN工作密钥
		@param keyId 密钥索引
		@param dependKeyId 依赖密钥索引用于解密得到明文
		@param cv 密钥校验值
		@param type 密钥类型 01 des密钥 02 SM密钥
		@return 0 成功其它失败。
				PED_ERR_INVALID_PARAM（-10000）参数非法
				PCIERR_INVALID_ARGUMENTS = -2000,  参数非法
				PCIERR_INVALID_TMKINDEX = -2001,非法密钥索引
				PCIERR_SAVE_TMK_FAILED = -2002,	保存密钥失败
				PCIERR_INVALID_PWK_INDEX = -2003,	非法密钥索引
				PCIERR_SAVE_PWK_FAILED = -2004,	 保存密钥失败
				PCIERR_READ_TMK_FAILED = -2005,  读取密钥失败
				PCIERR_USER_CANCEL		 = -2006, 用户取消
				PCIERR_USER_SKIP		 = -2007, 用户bypass
				PCIERR_READ_PWK_FAILED 	= -2008,  读取密钥失败
				PCIERR_READ_MWK_FAILED	= -2009, 读密钥失败
				PCIERR_READ_DWK_FAILED	= -2010, 读密钥失败
				PCIERR_READ_KEK_FAILED = -2011, 读密钥失败
	*/
	int storePinWK(in byte[] key, int keyId, int dependKeyId, in byte[] cv, int type);
	/**
		存储数据加密密钥
		@param key 工作密钥密文
		@param keyId 密钥索引
		@param dependKeyId 依赖密钥索引用于解密得到明文
		@param cv 密钥校验值
		@param type 密钥类型 01 des密钥 02 SM密钥
		@return 0 成功其它失败。
				PED_ERR_INVALID_PARAM（-10000）参数非法
				PCIERR_INVALID_ARGUMENTS = -2000,  参数非法
				PCIERR_INVALID_TMKINDEX = -2001,非法密钥索引
				PCIERR_SAVE_TMK_FAILED = -2002,	保存密钥失败
				PCIERR_INVALID_PWK_INDEX = -2003,	非法密钥索引
				PCIERR_SAVE_PWK_FAILED = -2004,	 保存密钥失败
				PCIERR_READ_TMK_FAILED = -2005,  读取密钥失败
				PCIERR_USER_CANCEL		 = -2006, 用户取消
				PCIERR_USER_SKIP		 = -2007, 用户bypass
				PCIERR_READ_PWK_FAILED 	= -2008,  读取密钥失败
				PCIERR_READ_MWK_FAILED	= -2009, 读密钥失败
				PCIERR_READ_DWK_FAILED	= -2010, 读密钥失败
				PCIERR_READ_KEK_FAILED = -2011, 读密钥失败
	*/
	int storeTDK(in byte[] key, int keyId, int dependKeyId, in byte[] cv, int type);
	/**
		存储终端MAC工作密钥
		@param key MAC工作密钥
		@param keyId 密钥索引
		@param dependKeyId 依赖密钥索引用于解密得到明文TMK
		@param cv 密钥校验值
		@param type 密钥类型 01 des密钥 02 SM密钥
		@return 0 成功其它失败。
				PED_ERR_INVALID_PARAM（-10000）参数非法
				PCIERR_INVALID_ARGUMENTS = -2000,  参数非法
				PCIERR_INVALID_TMKINDEX = -2001,非法密钥索引
				PCIERR_SAVE_TMK_FAILED = -2002,	保存密钥失败
				PCIERR_INVALID_PWK_INDEX = -2003,	非法密钥索引
				PCIERR_SAVE_PWK_FAILED = -2004,	 保存密钥失败
				PCIERR_READ_TMK_FAILED = -2005,  读取密钥失败
				PCIERR_USER_CANCEL		 = -2006, 用户取消
				PCIERR_USER_SKIP		 = -2007, 用户bypass
				PCIERR_READ_PWK_FAILED 	= -2008,  读取密钥失败
				PCIERR_READ_MWK_FAILED	= -2009, 读密钥失败
				PCIERR_READ_DWK_FAILED	= -2010, 读密钥失败
				PCIERR_READ_KEK_FAILED = -2011, 读密钥失败
	*/
	int storeMacWK(in byte[] key, int keyId, int dependKeyId, in byte[] cv, int type);
}