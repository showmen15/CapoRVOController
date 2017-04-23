package pl.agh.capo.rvo;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/*
 * Agent.cs
 * rvo Library C#
 *
 * Copyright 2008 University of North Carolina at Chapel Hill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http: //www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Please send all bug reports to <geom@cs.unc.edu>.
 *
 * The authors may be contacted via:
 *
 * Jur van den Berg, Stephen J. Guy, Jamie Snape, Ming C. Lin, Dinesh Manocha
 * Dept. of Computer Science
 * 201 S. Columbia St.
 * Frederick P. Brooks, Jr. Computer Science Bldg.
 * Chapel Hill, N.C. 27599-3175
 * United States of America
 *
 * <http: //gamma.cs.unc.edu/rvo/>
 */


/**
 * <summary>Defines an agent in the simulation.</summary>
 */
public class Agent
{
	public List<Map.Entry<Float, Agent>> agentNeighbors_ = new ArrayList<Map.Entry<Float, Agent>>();
	public List<Map.Entry<Float, Obstacle>> obstacleNeighbors_ = new ArrayList<Map.Entry<Float, Obstacle>>();
	public List<Line> orcaLines_ = new ArrayList<Line>();

	public Vector2 position_ = new Vector2();
	public Vector2 prefVelocity_ = new Vector2();
	public Vector2 velocity_ = new Vector2();
	public int id_ = 0;
	public int maxNeighbors_ = 0;
	public float maxSpeed_ = 0.0f;
	public float neighborDist_ = 0.0f;
	public float radius_ = 0.0f;
	public float timeHorizon_ = 0.0f;
	public float timeHorizonObst_ = 0.0f;

	public Vector2 newVelocity_ = new Vector2();
	
	public float timeStep_;


	  public Boolean IsCollide()
      {	  
		  return orcaLines_.size() > 0;
      }
	
	/**
	 * <summary>Computes the neighbors of this agent.</summary>
	 */
	public final void  computeNeighbors(KdTree kdTree_)
    {
        obstacleNeighbors_.clear();
        float rangeSq = RVOMath.sqr(timeHorizonObst_ * maxSpeed_ + radius_);
        kdTree_.computeObstacleNeighbors(this, rangeSq);

        agentNeighbors_.clear();

        if (maxNeighbors_ > 0)
        {
            rangeSq = RVOMath.sqr(neighborDist_);
                       
            RefObject<Float> tempRef_rangeSq = new RefObject<Float>(rangeSq);
			kdTree_.computeAgentNeighbors(this, tempRef_rangeSq);
			rangeSq = tempRef_rangeSq.argValue;		
        }
    }
	
	/**
	 * <summary>Computes the new velocity of this agent.</summary>
	 */
	public final void computeNewVelocity()
	{
		orcaLines_.clear();

		float invTimeHorizonObst = 1.0f / timeHorizonObst_;

		/* Create obstacle ORCA lines. */
		for (int i = 0; i < obstacleNeighbors_.size(); ++i)
		{

			Obstacle obstacle1 = obstacleNeighbors_.get(i).getValue();
			Obstacle obstacle2 = obstacle1.next_;

			Vector2 relativePosition1 = Vector2.OpSubtraction(obstacle1.point_, position_);
			Vector2 relativePosition2 = Vector2.OpSubtraction(obstacle2.point_ , position_);

			/*
			 * Check if velocity obstacle of obstacle is already taken care
			 * of by previously constructed obstacle ORCA lines.
			 */
			boolean alreadyCovered = false;

			for (int j = 0; j < orcaLines_.size(); ++j)
			{
				if (RVOMath.det(pl.agh.capo.rvo.Vector2.OpSubtraction(pl.agh.capo.rvo.Vector2.OpMultiply(invTimeHorizonObst, relativePosition1.clone()), orcaLines_.get(j).point), orcaLines_.get(j).direction.clone()) - invTimeHorizonObst * radius_ >= -RVOMath.RVO_EPSILON && RVOMath.det(pl.agh.capo.rvo.Vector2.OpSubtraction(pl.agh.capo.rvo.Vector2.OpMultiply(invTimeHorizonObst, relativePosition2.clone()), orcaLines_.get(j).point), orcaLines_.get(j).direction.clone()) - invTimeHorizonObst * radius_ >= -RVOMath.RVO_EPSILON)
				{
					alreadyCovered = true;

					break;
				}
			}

			if (alreadyCovered)
			{
				continue;
			}

			/* Not yet covered. Check for collisions. */
			float distSq1 = RVOMath.absSq(relativePosition1.clone());
			float distSq2 = RVOMath.absSq(relativePosition2.clone());

			float radiusSq = RVOMath.sqr(radius_);

			Vector2 obstacleVector = Vector2.OpSubtraction(obstacle2.point_ , obstacle1.point_);			
			float s = Vector2.OpMultiply(Vector2.OpUnaryNegation(relativePosition1) , obstacleVector) / RVOMath.absSq(obstacleVector);
			float distSqLine = RVOMath.absSq( Vector2.OpSubtraction(Vector2.OpUnaryNegation(relativePosition1),Vector2.OpMultiply(s, obstacleVector.clone())));
			
			Line line = new Line();

			if (s < 0.0f && distSq1 <= radiusSq)
			{
				/* Collision with left vertex. Ignore if non-convex. */
				if (obstacle1.convex_)
				{
					line.point = new Vector2(0.0f, 0.0f);
					line.direction = RVOMath.normalize(new Vector2(-relativePosition1.y(), relativePosition1.x()));
					orcaLines_.add(line.clone());
				}

				continue;
			}
			else if (s > 1.0f && distSq2 <= radiusSq)
			{
				/*
				 * Collision with right vertex. Ignore if non-convex or if
				 * it will be taken care of by neighboring obstacle.
				 */
				if (obstacle2.convex_ && RVOMath.det(relativePosition2.clone(), obstacle2.direction_.clone()) >= 0.0f)
				{
					line.point = new Vector2(0.0f, 0.0f);
					line.direction = RVOMath.normalize(new Vector2(-relativePosition2.y(), relativePosition2.x()));
					orcaLines_.add(line.clone());
				}

				continue;
			}
			else if (s >= 0.0f && s < 1.0f && distSqLine <= radiusSq)
			{
				/* Collision with obstacle segment. */
				line.point = new Vector2(0.0f, 0.0f);
				line.direction = Vector2.OpUnaryNegation(obstacle1.direction_);
				orcaLines_.add(line.clone());

				continue;
			}

			/*
			 * No collision. Compute legs. When obliquely viewed, both legs
			 * can come from a single vertex. Legs extend cut-off line when
			 * non-convex vertex.
			 */

			Vector2 leftLegDirection = new Vector2();
			Vector2 rightLegDirection = new Vector2();

			if (s < 0.0f && distSqLine <= radiusSq)
			{
				/*
				 * Obstacle viewed obliquely so that left vertex
				 * defines velocity obstacle.
				 */
				if (!obstacle1.convex_)
				{
					/* Ignore obstacle. */
					continue;
				}

				obstacle2 = obstacle1;

				float leg1 = RVOMath.sqrt(distSq1 - radiusSq);
				leftLegDirection = Vector2.OpDivision(new Vector2(relativePosition1.x() * leg1 - relativePosition1.y() * radius_, relativePosition1.x() * radius_ + relativePosition1.y() * leg1), distSq1);
				rightLegDirection = Vector2.OpDivision(new Vector2(relativePosition1.x() * leg1 + relativePosition1.y() * radius_, -relativePosition1.x() * radius_ + relativePosition1.y() * leg1), distSq1);
			}
			else if (s > 1.0f && distSqLine <= radiusSq)
			{
				/*
				 * Obstacle viewed obliquely so that
				 * right vertex defines velocity obstacle.
				 */
				if (!obstacle2.convex_)
				{
					/* Ignore obstacle. */
					continue;
				}

				obstacle1 = obstacle2;

				float leg2 = RVOMath.sqrt(distSq2 - radiusSq);
				leftLegDirection = Vector2.OpDivision(new Vector2(relativePosition2.x() * leg2 - relativePosition2.y() * radius_, relativePosition2.x() * radius_ + relativePosition2.y() * leg2), distSq2);
				rightLegDirection = Vector2.OpDivision(new Vector2(relativePosition2.x() * leg2 + relativePosition2.y() * radius_, -relativePosition2.x() * radius_ + relativePosition2.y() * leg2) , distSq2);
			}
			else
			{
				/* Usual situation. */
				if (obstacle1.convex_)
				{
					float leg1 = RVOMath.sqrt(distSq1 - radiusSq);
					leftLegDirection = Vector2.OpDivision(new Vector2(relativePosition1.x() * leg1 - relativePosition1.y() * radius_, relativePosition1.x() * radius_ + relativePosition1.y() * leg1), distSq1);
				}
				else
				{
					/* Left vertex non-convex; left leg extends cut-off line. */
					leftLegDirection = Vector2.OpUnaryNegation(obstacle1.direction_);
				}

				if (obstacle2.convex_)
				{
					float leg2 = RVOMath.sqrt(distSq2 - radiusSq);
					rightLegDirection = Vector2.OpDivision(new Vector2(relativePosition2.x() * leg2 + relativePosition2.y() * radius_, -relativePosition2.x() * radius_ + relativePosition2.y() * leg2), distSq2);
				}
				else
				{
					/* Right vertex non-convex; right leg extends cut-off line. */
					rightLegDirection = obstacle1.direction_.clone();
				}
			}

			/*
			 * Legs can never point into neighboring edge when convex
			 * vertex, take cutoff-line of neighboring edge instead. If
			 * velocity projected on "foreign" leg, no constraint is added.
			 */

			Obstacle leftNeighbor = obstacle1.previous_;

			boolean isLeftLegForeign = false;
			boolean isRightLegForeign = false;

			if (obstacle1.convex_ && RVOMath.det(leftLegDirection.clone(), Vector2.OpUnaryNegation(leftNeighbor.direction_)) >= 0.0f)
			{
				/* Left leg points into obstacle. */
				leftLegDirection = Vector2.OpUnaryNegation(leftNeighbor.direction_);
				isLeftLegForeign = true;
			}

			if (obstacle2.convex_ && RVOMath.det(rightLegDirection.clone(), obstacle2.direction_.clone()) <= 0.0f)
			{
				/* Right leg points into obstacle. */
				rightLegDirection = obstacle2.direction_.clone();
				isRightLegForeign = true;
			}

			/* Compute cut-off centers. */
			Vector2 leftCutOff = Vector2.OpMultiply(invTimeHorizonObst , Vector2.OpSubtraction(obstacle1.point_ , position_));
			Vector2 rightCutOff = Vector2.OpMultiply(invTimeHorizonObst , Vector2.OpSubtraction(obstacle2.point_ , position_));
			Vector2 cutOffVector = pl.agh.capo.rvo.Vector2.OpSubtraction(rightCutOff.clone(), leftCutOff.clone());

			/* Project current velocity on velocity obstacle. */

			/* Check if current velocity is projected on cutoff circles. */
			
			float t = obstacle1 == obstacle2 ? 0.5f : (Vector2.OpMultiply((Vector2.OpSubtraction(velocity_ , leftCutOff)), cutOffVector)) / RVOMath.absSq(cutOffVector);
						
			float tLeft = pl.agh.capo.rvo.Vector2.OpMultiply(pl.agh.capo.rvo.Vector2.OpSubtraction(velocity_, leftCutOff.clone()), leftLegDirection.clone());
			float tRight = pl.agh.capo.rvo.Vector2.OpMultiply(pl.agh.capo.rvo.Vector2.OpSubtraction(velocity_, rightCutOff.clone()), rightLegDirection.clone());

			if ((t < 0.0f && tLeft < 0.0f) || (obstacle1 == obstacle2 && tLeft < 0.0f && tRight < 0.0f))
			{
				/* Project on left cut-off circle. */
				Vector2 unitW = RVOMath.normalize(pl.agh.capo.rvo.Vector2.OpSubtraction(velocity_, leftCutOff.clone()));

				line.direction = new Vector2(unitW.y(), -unitW.x());
				line.point = pl.agh.capo.rvo.Vector2.OpAddition(leftCutOff.clone(), pl.agh.capo.rvo.Vector2.OpMultiply(radius_ * invTimeHorizonObst, unitW.clone()));
				orcaLines_.add(line.clone());

				continue;
			}
			else if (t > 1.0f && tRight < 0.0f)
			{
				/* Project on right cut-off circle. */
				Vector2 unitW = RVOMath.normalize(pl.agh.capo.rvo.Vector2.OpSubtraction(velocity_, rightCutOff.clone()));

				line.direction = new Vector2(unitW.y(), -unitW.x());
				line.point = pl.agh.capo.rvo.Vector2.OpAddition(rightCutOff.clone(), pl.agh.capo.rvo.Vector2.OpMultiply(radius_ * invTimeHorizonObst, unitW.clone()));
				orcaLines_.add(line.clone());

				continue;
			}

			/*
			 * Project on left leg, right leg, or cut-off line, whichever is
			 * closest to velocity.
			 */
			float distSqCutoff = (t < 0.0f || t > 1.0f || obstacle1 == obstacle2) ? Float.POSITIVE_INFINITY : RVOMath.absSq(pl.agh.capo.rvo.Vector2.OpSubtraction(velocity_, pl.agh.capo.rvo.Vector2.OpAddition(leftCutOff.clone(), pl.agh.capo.rvo.Vector2.OpMultiply(t, cutOffVector.clone()))));
			float distSqLeft = tLeft < 0.0f ? Float.POSITIVE_INFINITY : RVOMath.absSq(pl.agh.capo.rvo.Vector2.OpSubtraction(velocity_, pl.agh.capo.rvo.Vector2.OpAddition(leftCutOff.clone(), pl.agh.capo.rvo.Vector2.OpMultiply(tLeft, leftLegDirection.clone()))));
			float distSqRight = tRight < 0.0f ? Float.POSITIVE_INFINITY : RVOMath.absSq(pl.agh.capo.rvo.Vector2.OpSubtraction(velocity_, pl.agh.capo.rvo.Vector2.OpAddition(rightCutOff.clone(), pl.agh.capo.rvo.Vector2.OpMultiply(tRight, rightLegDirection.clone()))));

			if (distSqCutoff <= distSqLeft && distSqCutoff <= distSqRight)
			{
				/* Project on cut-off line. */
				line.direction = Vector2.OpUnaryNegation(obstacle1.direction_);
				line.point = pl.agh.capo.rvo.Vector2.OpAddition(leftCutOff.clone(), Vector2.OpMultiply(radius_ * invTimeHorizonObst , new Vector2(-line.direction.y(), line.direction.x())));
				orcaLines_.add(line.clone());

				continue;
			}

			if (distSqLeft <= distSqRight)
			{
				/* Project on left leg. */
				if (isLeftLegForeign)
				{
					continue;
				}

				line.direction = leftLegDirection.clone();
				line.point = pl.agh.capo.rvo.Vector2.OpAddition(leftCutOff.clone(), Vector2.OpMultiply(radius_ * invTimeHorizonObst , new Vector2(-line.direction.y(), line.direction.x())));
				orcaLines_.add(line.clone());

				continue;
			}

			/* Project on right leg. */
			if (isRightLegForeign)
			{
				continue;
			}

			line.direction = pl.agh.capo.rvo.Vector2.OpUnaryNegation(rightLegDirection.clone());
			line.point = pl.agh.capo.rvo.Vector2.OpAddition(rightCutOff.clone(), Vector2.OpMultiply(radius_ * invTimeHorizonObst, new Vector2(-line.direction.y(), line.direction.x())));
			orcaLines_.add(line.clone());
		}

		int numObstLines = orcaLines_.size();

		float invTimeHorizon = 1.0f / timeHorizon_;

		/* Create agent ORCA lines. */
		for (int i = 0; i < agentNeighbors_.size(); ++i)
		{
			Agent other = agentNeighbors_.get(i).getValue();

			Vector2 relativePosition = Vector2.OpSubtraction(other.position_ , position_);
			Vector2 relativeVelocity = Vector2.OpSubtraction(velocity_ , other.velocity_);
			float distSq = RVOMath.absSq(relativePosition.clone());
			float combinedRadius = radius_ + other.radius_;
			float combinedRadiusSq = RVOMath.sqr(combinedRadius);

			Line line = new Line();
			Vector2 u = new Vector2();

			if (distSq > combinedRadiusSq)
			{
				/* No collision. */
				Vector2 w = pl.agh.capo.rvo.Vector2.OpSubtraction(relativeVelocity.clone(), pl.agh.capo.rvo.Vector2.OpMultiply(invTimeHorizon, relativePosition.clone()));

				/* Vector from cutoff center to relative velocity. */
				float wLengthSq = RVOMath.absSq(w.clone());
				float dotProduct1 = pl.agh.capo.rvo.Vector2.OpMultiply(w.clone(), relativePosition.clone());

				if (dotProduct1 < 0.0f && RVOMath.sqr(dotProduct1) > combinedRadiusSq * wLengthSq)
				{
					/* Project on cut-off circle. */
					float wLength = RVOMath.sqrt(wLengthSq);
					Vector2 unitW = pl.agh.capo.rvo.Vector2.OpDivision(w.clone(), wLength);

					line.direction = new Vector2(unitW.y(), -unitW.x());
					u = pl.agh.capo.rvo.Vector2.OpMultiply((combinedRadius * invTimeHorizon - wLength), unitW.clone());
				}
				else
				{
					/* Project on legs. */
					float leg = RVOMath.sqrt(distSq - combinedRadiusSq);

					if (RVOMath.det(relativePosition.clone(), w.clone()) > 0.0f)
					{
						/* Project on left leg. */
						line.direction = Vector2.OpDivision(new Vector2(relativePosition.x() * leg - relativePosition.y() * combinedRadius, relativePosition.x() * combinedRadius + relativePosition.y() * leg), distSq);
					}
					else
					{
						/* Project on right leg. */						
						line.direction = Vector2.OpDivision(Vector2.OpUnaryNegation(new Vector2(relativePosition.x() * leg + relativePosition.y() * combinedRadius, -relativePosition.x() * combinedRadius + relativePosition.y() * leg)) , distSq);
					}

					float dotProduct2 = pl.agh.capo.rvo.Vector2.OpMultiply(relativeVelocity.clone(), line.direction);
					u = pl.agh.capo.rvo.Vector2.OpSubtraction(Vector2.OpMultiply(dotProduct2 , line.direction), relativeVelocity.clone());
				}
			}
			else
			{
				/* Collision. Project on cut-off circle of time timeStep. */
				float invTimeStep = 1.0f / timeStep_;

				/* Vector from cutoff center to relative velocity. */
				Vector2 w = pl.agh.capo.rvo.Vector2.OpSubtraction(relativeVelocity.clone(), pl.agh.capo.rvo.Vector2.OpMultiply(invTimeStep, relativePosition.clone()));

				float wLength = RVOMath.abs(w.clone());
				Vector2 unitW = pl.agh.capo.rvo.Vector2.OpDivision(w.clone(), wLength);

				line.direction = new Vector2(unitW.y(), -unitW.x());
				u = pl.agh.capo.rvo.Vector2.OpMultiply((combinedRadius * invTimeStep - wLength), unitW.clone());
			}

			line.point = pl.agh.capo.rvo.Vector2.OpAddition(velocity_, pl.agh.capo.rvo.Vector2.OpMultiply(0.5f, u.clone()));
			orcaLines_.add(line.clone());
		}

		RefObject<pl.agh.capo.rvo.Vector2> tempRef_newVelocity_ = new RefObject<pl.agh.capo.rvo.Vector2>(newVelocity_);
		int lineFail = linearProgram2(orcaLines_, maxSpeed_, prefVelocity_.clone(), false, tempRef_newVelocity_);
	newVelocity_ = tempRef_newVelocity_.argValue;

		if (lineFail < orcaLines_.size())
		{
			RefObject<pl.agh.capo.rvo.Vector2> tempRef_newVelocity_2 = new RefObject<pl.agh.capo.rvo.Vector2>(newVelocity_);
			linearProgram3(orcaLines_, numObstLines, lineFail, maxSpeed_, tempRef_newVelocity_2);
		newVelocity_ = tempRef_newVelocity_2.argValue;
		}
	}

	/**
	 * <summary>Inserts an agent neighbor into the set of neighbors of this
	 * agent.</summary>
	 *
	 * <param name="agent">A pointer to the agent to be inserted.</param>
	 * <param name="rangeSq">The squared range around this agent.</param>
	 */
	public final void insertAgentNeighbor(Agent agent,RefObject<Float> rangeSq)
	{
		if (this.id_ != agent.id_)
		{
			float distSq = RVOMath.absSq(Vector2.OpSubtraction(position_ , agent.position_));
			
			
			if (distSq < rangeSq.argValue)
			{
				if (agentNeighbors_.size() < maxNeighbors_)
				{				
					agentNeighbors_.add( new SimpleEntry<Float, Agent>(distSq, agent));
				}

				int i = agentNeighbors_.size() - 1;

				while (i != 0 && distSq < agentNeighbors_.get(i - 1).getKey())
				{
					agentNeighbors_.set(i, agentNeighbors_.get(i - 1));
					--i;
				}

				agentNeighbors_.set(i, new SimpleEntry<Float, Agent>(distSq, agent));

				if (agentNeighbors_.size() == maxNeighbors_)
				{
					rangeSq.argValue = agentNeighbors_.get(agentNeighbors_.size() - 1).getKey();
				}
			}
		}
	}

	/**
	 * <summary>Inserts a static obstacle neighbor into the set of neighbors
	 * of this agent.</summary>
	 *
	 * <param name="obstacle">The number of the static obstacle to be
	 * inserted.</param>
	 * <param name="rangeSq">The squared range around this agent.</param>
	 */
	public final void insertObstacleNeighbor(Obstacle obstacle, float rangeSq)
	{
		Obstacle nextObstacle = obstacle.next_;

		float distSq = RVOMath.distSqPointLineSegment(obstacle.point_.clone(), nextObstacle.point_.clone(), position_.clone());

		if (distSq < rangeSq)
		{
			obstacleNeighbors_.add(new SimpleEntry<Float, Obstacle>(distSq, obstacle));

			int i = obstacleNeighbors_.size() - 1;

			while (i != 0 && distSq < obstacleNeighbors_.get(i - 1).getKey())
			{
				obstacleNeighbors_.set(i, obstacleNeighbors_.get(i - 1));
				--i;
			}
			obstacleNeighbors_.set(i, new SimpleEntry<Float, Obstacle>(distSq, obstacle));
		}
	}

	/**
	 * <summary>Updates the two-dimensional position and two-dimensional
	 * velocity of this agent.</summary>
	 */
	public final void update()
	{
		if(Math.abs(newVelocity_.x_) < 0.00001)
			newVelocity_.x_ = 0.0f;
		
		if(Math.abs(newVelocity_.y_) < 0.00001)
			newVelocity_.y_ = 0.0f;
		
		velocity_ = newVelocity_.clone();
		//position_ = Vector2.OpAddition(position_ , Vector2.OpMultiply(velocity_ , timeStep_));
	}

	/**
	 * <summary>Solves a one-dimensional linear program on a specified line
	 * subject to linear constraints defined by lines and a circular
	 * constraint.</summary>
	 *
	 * <returns>True if successful.</returns>
	 *
	 * <param name="lines">Lines defining the linear constraints.</param>
	 * <param name="lineNo">The specified line constraint.</param>
	 * <param name="radius">The radius of the circular constraint.</param>
	 * <param name="optVelocity">The optimization velocity.</param>
	 * <param name="directionOpt">True if the direction should be optimized.
	 * </param>
	 * <param name="result">A reference to the result of the linear program.
	 * </param>
	 */
	private boolean linearProgram1(List<Line> lines, int lineNo, float radius, Vector2 optVelocity, boolean directionOpt, RefObject<Vector2> result)
	{
		float dotProduct = Vector2.OpMultiply(lines.get(lineNo).point , lines.get(lineNo).direction);
		float discriminant = RVOMath.sqr(dotProduct) + RVOMath.sqr(radius) - RVOMath.absSq(lines.get(lineNo).point.clone());

		if (discriminant < 0.0f)
		{
			/* Max speed circle fully invalidates line lineNo. */
			return false;
		}

		float sqrtDiscriminant = RVOMath.sqrt(discriminant);
		float tLeft = -dotProduct - sqrtDiscriminant;
		float tRight = -dotProduct + sqrtDiscriminant;

		for (int i = 0; i < lineNo; ++i)
		{
			float denominator = RVOMath.det(lines.get(lineNo).direction.clone(), lines.get(i).direction.clone());
			float numerator = RVOMath.det(lines.get(i).direction.clone(),  Vector2.OpSubtraction(lines.get(lineNo).point , lines.get(i).point));

			if (RVOMath.fabs(denominator) <= RVOMath.RVO_EPSILON)
			{
				/* Lines lineNo and i are (almost) parallel. */
				if (numerator < 0.0f)
				{
					return false;
				}

				continue;
			}

			float t = numerator / denominator;

			if (denominator >= 0.0f)
			{
				/* Line i bounds line lineNo on the right. */
				tRight = Math.min(tRight, t);
			}
			else
			{
				/* Line i bounds line lineNo on the left. */
				tLeft = Math.max(tLeft, t);
			}

			if (tLeft > tRight)
			{
				return false;
			}
		}

		if (directionOpt)
		{
			/* Optimize direction. */
			if (pl.agh.capo.rvo.Vector2.OpMultiply(optVelocity.clone(), lines.get(lineNo).direction) > 0.0f)
			{
				/* Take right extreme. */
				result.argValue = Vector2.OpAddition( lines.get(lineNo).point , Vector2.OpMultiply(tRight , lines.get(lineNo).direction));
			}
			else
			{
				/* Take left extreme. */
				result.argValue = Vector2.OpAddition(lines.get(lineNo).point , Vector2.OpMultiply(tLeft , lines.get(lineNo).direction));
			}
		}
		else
		{
			/* Optimize closest point. */
			float t = pl.agh.capo.rvo.Vector2.OpMultiply(lines.get(lineNo).direction, pl.agh.capo.rvo.Vector2.OpSubtraction(optVelocity.clone(), lines.get(lineNo).point));

			if (t < tLeft)
			{
				result.argValue = Vector2.OpAddition(lines.get(lineNo).point , Vector2.OpMultiply(tLeft , lines.get(lineNo).direction));
			}
			else if (t > tRight)
			{
				result.argValue = Vector2.OpAddition(lines.get(lineNo).point , Vector2.OpMultiply(tRight , lines.get(lineNo).direction));
			}
			else
			{
				result.argValue = Vector2.OpAddition(lines.get(lineNo).point , Vector2.OpMultiply(t , lines.get(lineNo).direction));
			}
		}

		return true;
	}

	/**
	 * <summary>Solves a two-dimensional linear program subject to linear
	 * constraints defined by lines and a circular constraint.</summary>
	 *
	 * <returns>The number of the line it fails on, and the number of lines
	 * if successful.</returns>
	 *
	 * <param name="lines">Lines defining the linear constraints.</param>
	 * <param name="radius">The radius of the circular constraint.</param>
	 * <param name="optVelocity">The optimization velocity.</param>
	 * <param name="directionOpt">True if the direction should be optimized.
	 * </param>
	 * <param name="result">A reference to the result of the linear program.
	 * </param>
	 */
	private int linearProgram2(List<Line> lines, float radius, Vector2 optVelocity, boolean directionOpt, RefObject<Vector2> result)
	{
		if (directionOpt)
		{
			/*
			 * Optimize direction. Note that the optimization velocity is of
			 * unit length in this case.
			 */
			result.argValue = pl.agh.capo.rvo.Vector2.OpMultiply(optVelocity.clone(), radius);
		}
		else if (RVOMath.absSq(optVelocity.clone()) > RVOMath.sqr(radius))
		{
			/* Optimize closest point and outside circle. */
			result.argValue = Vector2.OpMultiply(RVOMath.normalize(optVelocity.clone()) , radius);
		}
		else
		{
			/* Optimize closest point and inside circle. */
			result.argValue = optVelocity.clone();
		}

		for (int i = 0; i < lines.size(); ++i)
		{
			if (RVOMath.det(lines.get(i).direction.clone(), pl.agh.capo.rvo.Vector2.OpSubtraction(lines.get(i).point, result.argValue.clone())) > 0.0f)
			{
				/* Result does not satisfy constraint i. Compute new optimal result. */
				Vector2 tempResult = result.argValue.clone();
				if (!linearProgram1(lines, i, radius, optVelocity.clone(), directionOpt, result))
				{
					result.argValue = tempResult.clone();

					return i;
				}
			}
		}

		return lines.size();
	}

	/**
	 * <summary>Solves a two-dimensional linear program subject to linear
	 * constraints defined by lines and a circular constraint.</summary>
	 *
	 * <param name="lines">Lines defining the linear constraints.</param>
	 * <param name="numObstLines">Count of obstacle lines.</param>
	 * <param name="beginLine">The line on which the 2-d linear program
	 * failed.</param>
	 * <param name="radius">The radius of the circular constraint.</param>
	 * <param name="result">A reference to the result of the linear program.
	 * </param>
	 */
	private void linearProgram3(List<Line> lines, int numObstLines, int beginLine, float radius, RefObject<Vector2> result)
	{
		float distance = 0.0f;

		for (int i = beginLine; i < lines.size(); ++i)
		{
			if (RVOMath.det(lines.get(i).direction.clone(), pl.agh.capo.rvo.Vector2.OpSubtraction(lines.get(i).point, result.argValue.clone())) > distance)
			{
				/* Result does not satisfy constraint of line i. */
				List<Line> projLines = new ArrayList<Line>();
				for (int ii = 0; ii < numObstLines; ++ii)
				{
					projLines.add(lines.get(ii));
				}

				for (int j = numObstLines; j < i; ++j)
				{
					Line line = new Line();

					float determinant = RVOMath.det(lines.get(i).direction.clone(), lines.get(j).direction.clone());

					if (RVOMath.fabs(determinant) <= RVOMath.RVO_EPSILON)
					{
						/* Line i and line j are parallel. */
						if (Vector2.OpMultiply(lines.get(i).direction, lines.get(j).direction) > 0.0f)
						{
							/* Line i and line j point in the same direction. */
							continue;
						}
						else
						{
							/* Line i and line j point in opposite direction. */
							line.point = Vector2.OpMultiply(0.5f , Vector2.OpAddition(lines.get(i).point , lines.get(j).point));
						}
					}
					else
					{
						line.point = Vector2.OpAddition(lines.get(i).point , Vector2.OpMultiply((RVOMath.det(lines.get(j).direction.clone(), Vector2.OpSubtraction(lines.get(i).point, lines.get(j).point)) / determinant), lines.get(i).direction));
					}

					line.direction = RVOMath.normalize(Vector2.OpSubtraction(lines.get(j).direction , lines.get(i).direction));
					projLines.add(line.clone());
				}

				Vector2 tempResult = result.argValue.clone();
				if (linearProgram2(projLines, radius, new Vector2(-lines.get(i).direction.y(), lines.get(i).direction.x()), true, result) < projLines.size())
				{
					/*
					 * This should in principle not happen. The result is by
					 * definition already in the feasible region of this
					 * linear program. If it fails, it is due to small
					 * floating point error, and the current result is kept.
					 */
					result.argValue = tempResult.clone();
				}

				distance = RVOMath.det(lines.get(i).direction.clone(), pl.agh.capo.rvo.Vector2.OpSubtraction(lines.get(i).point, result.argValue.clone()));
			}
		}
	}
}