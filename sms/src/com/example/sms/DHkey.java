package com.example.sms;

import java.math.BigInteger;

/*
 * Diffie-Hellman��ȫ�㷨�Ĺ�����ʵ�� 
 * �㷨����:
 * �����û�A��Bϣ������һ����Կ���û�Aѡ��һ����Ϊ˽����Կ�������XA(XA<q)�������㹫����ԿYA=g^XA mod p
 * A��XA��ֵ���ܴ�Ŷ�ʹYA�ܱ�B������á����Ƶأ��û�Bѡ��һ��˽�е������XB<q�������㹫����ԿYB=g^XB mod p
 * B��XB��ֵ���ܴ�Ŷ�ʹYB�ܱ�A�������.
 * �û�A��������������Կ�ļ��㷽ʽ��K = (YB)^XA mod p 
 * �û�B��������������Կ�ļ��㷽ʽ��K = (YA)^XB mod p
 * 
 * g���������ӿڲ��ù̶������֣�����ӿ�ʹ��
 * 
 * x��˽Կ�������û�������ʽ
 * 
 * p��������ʹ��Java�ӿڻ�ȡ������
 * 
 * y:��Կ�����������
 * 
 * k:��Կ�����������
 */
public class DHkey {
	
	private BigInteger g;
	private BigInteger x;
	private BigInteger p;
	private BigInteger y;
	private BigInteger k;
	
	public DHkey(String x){
		this.p = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA18217C32905E462E36CE3BE39E772C180E86039B2783A2EC07A28FB5C55DF06F4C52C9DE2BCBF6955817183995497CEA956AE515D2261898FA051015728E5A8AACAA68FFFFFFFFFFFFFFFF",16);
		this.g = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF",16);
		this.x =new BigInteger(x,16);
	}
	/*
	 * ���ɹ�Կ�������Լ���˽Կ
	 *
	 */
	public BigInteger getY(){
		y = g.modPow(x, p);
		return y;
	}
	/*
	 * ���ɶԳ���Կ
	 *  @param y
	 *           �Է������Ĺ�Կ
	 */
	public BigInteger getK(String y){
		
		BigInteger y2 = new BigInteger(y);
		
		k = y2.modPow(this.x, this.p);
		
		return k;
	}
	
	

}
