package sc.tps;

import sc.Distance;
import Jama.Matrix;

public class TPS {
	Matrix c, L;
	double E;
	public double[][] Z;
	double meanDistance;
	double eps = 0.000000001;

	public void bookStein(double[][] X, double[][] Y, double beta) {
//		beta = 0;
		double[][] r2 = Distance.dist2(X);
		double[][] l = new double[X.length + 3][X.length + 3];
		meanDistance = 0;
		// fill K
		for (int i = 0; i < X.length; i++) {
			for (int j = 0; j < X.length; j++) {
				l[i][j] = r2[i][j]
						* Math.log(r2[i][j] + ((i == j) ? 1. : 0) + eps);
				if (i != j) {
					meanDistance = r2[i][j];
				}
			}
		}
		meanDistance = meanDistance / (X.length * X.length - X.length);
		// fill P , PT
		for (int i = 0; i < X.length; i++) {
			l[i][X.length] = 1;
			l[i][X.length + 1] = X[i][0];
			l[i][X.length + 2] = X[i][1];
			l[X.length][i] = 1;
			l[X.length + 1][i] = X[i][0];
			l[X.length + 2][i] = X[i][1];
		}
		
		

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				l[X.length + i][X.length + j] = 0;

			}
		}

		L = new Matrix(l);
		Matrix K = L.getMatrix(0, X.length - 1, 0, X.length - 1);
		for (int i = 0; i < X.length; i++) {
			l[i][i] += beta * meanDistance  * meanDistance;
//			l[i][i] += 404792;
		}
		L = new Matrix(l);
//		L.print(10, 10);

		
		double[][] v = new double[X.length + 3][2];

		for (int i = 0; i < X.length; i++) {
			v[i][0] = Y[i][0];
			v[i][1] = Y[i][1];
		}

		for (int i = 0; i < 3; i++) {
			v[i + X.length][0] = 0;
			v[i + X.length][1] = 0;
		}
		Matrix V = new Matrix(v);
//		V.print(10, 10);
		c = L.inverse().times(V);
//		L.inverse().times(L).print(1, 20);
//		c.print(10, 10);

		Matrix c1 = c.getMatrix(0, X.length - 1, 0, 0);
		Matrix c2 = c.getMatrix(0, X.length - 1, 0, 0).transpose();
//		K.print(10, 10);
		Matrix Q = c2.times(K).times(c1);
		E = Q.get(0, 0);
//		System.err.println(E);
		Z = new double[X.length][2];
		double[][] u = Distance.dist2(X, X);
		for (int i = 0; i < u.length; i++) {
			for (int j = 0; j < u[0].length; j++) {
				u[i][j] = u[i][j] * Math.log(u[i][j] + eps);
			}
		}

//		Matrix uMatrix = new Matrix(u);
//		uMatrix.print(10, 10);
		
		for (int i = 0; i < Z.length; i++) {
			Z[i][0] = 1 * c.get(X.length + 0, 0) + X[i][0] * c.get(X.length + 1, 0) + X[i][1]
					* c.get(X.length + 2, 0);
			for (int j = 0; j < u.length; j++) {
				Z[i][0] += c.get( j, 0) * u[i][j];
			}

			Z[i][1] = 1 * c.get(X.length + 0, 1) + X[i][0] * c.get(X.length + 1, 1) + X[i][1]
					* c.get(X.length + 2, 1);
			for (int j = 0; j < u.length; j++) {
				Z[i][1] += c.get( j, 1) * u[i][j];
			}
		}

		// fx_aff=cx(n_good+1:n_good+3)'*[ones(1,nsamp); X'];
		// d2=max(dist2(X3b,X),0);
		// U=d2.*log(d2+eps);
		// fx_wrp=cx(1:n_good)'*U;
		// fx=fx_aff+fx_wrp;
		// fy_aff=cy(n_good+1:n_good+3)'*[ones(1,nsamp); X'];
		// fy_wrp=cy(1:n_good)'*U;
		// fy=fy_aff+fy_wrp;
		//
		// Z=[fx; fy]';
		// for (int i = 0; i < Z.length; i++) {
		// Z[i][0] =

		// }

		// recomplete X;

	}

	protected Matrix eye(int N) {
		double[][] eye = new double[N][N];
		for (int i = 0; i < eye.length; i++) {
			for (int j = 0; j < eye.length; j++) {
				eye[i][j] = 0;
			}
		}

		for (int i = 0; i < eye.length; i++) {
			eye[i][i] = 1;
		}
		return new Matrix(eye);
	}
}
