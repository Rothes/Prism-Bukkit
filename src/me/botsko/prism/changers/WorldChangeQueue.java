package me.botsko.prism.changers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.actions.Action;
import me.botsko.prism.actions.ActionType;
import me.botsko.prism.actions.BlockAction;
import me.botsko.prism.actions.EntityAction;
import me.botsko.prism.actions.ItemStackAction;
import me.botsko.prism.actions.SignAction;
import me.botsko.prism.appliers.ApplierResult;
import me.botsko.prism.appliers.Restore;
import me.botsko.prism.appliers.Rollback;
import me.botsko.prism.events.BlockStateChange;
import me.botsko.prism.events.PrismBlocksRollbackEvent;
import me.botsko.prism.events.PrismProcessType;
import me.botsko.prism.utils.EntityUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;

public class WorldChangeQueue {
	
	/**
	 * 
	 */
	protected Prism plugin;
	
	/**
	 * 
	 */
	PrismProcessType processType;
	
	/**
	 * 
	 */
	protected List<Action> results;
	
	/**
	 * 
	 */
	protected Player player;
	
	/**
	 * 
	 */
	protected boolean is_preview = false;
	
	/**
	 * 
	 */
	protected QueryParameters parameters;
	
	/**
	 * 
	 */
	int skipped_block_count;
	
	/**
	 * 
	 */
	int changes_applied_count;
	
	
	/**
	 * 
	 * @param processType
	 */
	public WorldChangeQueue( Prism plugin, PrismProcessType processType, List<Action> incomingResults, Player player, boolean is_preview, QueryParameters parameters ){
		this.plugin = plugin;
		this.processType = processType;
		this.results = incomingResults;
		this.player = player;
		this.is_preview = is_preview;
		this.parameters = parameters;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public ApplierResult apply(){
		
		ArrayList<Action> deferredChanges = new ArrayList<Action>();
		ArrayList<BlockStateChange> blockStateChanges = new ArrayList<BlockStateChange>();
		
		if(results != null && !results.isEmpty()){
			
			for(Action a : results){;
				
				// No sense in trying to rollback
				// when the type doesn't support it.
				if( processType.equals(PrismProcessType.ROLLBACK) && !a.getType().canRollback()){
					continue;
				}
				
				// No sense in trying to restore
				// when the type doesn't support it.
				if( processType.equals(PrismProcessType.RESTORE) && !a.getType().canRestore()){
					continue;
				}
					
				// Determine the location
				World world = plugin.getServer().getWorld(a.getWorld_name());
				Location loc = new Location(world, a.getX(), a.getY(), a.getZ());
	
				
				/**
				 * Reverse or restore block changes
				 */
				if( a instanceof BlockAction ){
					
					// Pass along to the change handler
					BlockChangeHandler blockChangeHandler = new BlockChangeHandler( PrismProcessType.ROLLBACK, loc, (BlockAction) a, player, is_preview );
					BlockChangeResult result = blockChangeHandler.applyChange();
					
					if(result.getChangeResultType().equals(ChangeResultType.DEFERRED)){
						deferredChanges.add( a );
					}
					else if(result.getChangeResultType().equals(ChangeResultType.SKIPPED)){
						skipped_block_count++;
						continue;
					} else {
						blockStateChanges.add(result.getBlockStateChange());
						changes_applied_count++;
					}
				}
				
				
				/**
				 * Rollback entity kills
				 */
				if( processType.equals(PrismProcessType.ROLLBACK) && a instanceof EntityAction ){
					
					EntityAction b = (EntityAction) a;
					
					if(!EntityUtils.mayEverSpawn(b.getEntityType())){
						skipped_block_count++;
						continue;
					}
					
					Entity entity = world.spawnEntity(loc, b.getEntityType());
					
					// Set sheep color
					if( entity.getType().equals(EntityType.SHEEP)){
						Sheep sheep = ((Sheep) entity);
						sheep.setColor( b.getColor() );
					}
					
					plugin.debug("Rolling back entity " + b.getEntityType().getName());
					
					changes_applied_count++;
					
				}
				
				
				/**
				 * Rollback itemstack actions
				 */
				if( processType.equals(PrismProcessType.ROLLBACK) && a instanceof ItemStackAction ){
					
					ItemStackAction b = (ItemStackAction) a;
					
					Block block = world.getBlockAt(loc);
					if(block.getType().equals(Material.CHEST)){
						Chest chest = (Chest) block.getState();
						
						// If item was removed, put it back.
						if(a.getType().equals(ActionType.ITEM_REMOVE) && plugin.getConfig().getBoolean("prism.appliers.allow_rollback_items_removed_from_container")){
							HashMap<Integer,ItemStack> leftovers = chest.getInventory().addItem( b.getItem() );
							changes_applied_count++;
							if(leftovers.size() > 0){
								plugin.debug("Couldn't rollback items to container, it's full.");
							}
						}
					}
				}
				

				/**
				 * Restore sign actions
				 */
				if( processType.equals(PrismProcessType.RESTORE) && a instanceof SignAction ){
					
					SignAction b = (SignAction) a;
					Block block = world.getBlockAt(loc);
					
					// Ensure a sign exists there (and no other block)
					if( block.getType().equals(Material.AIR) || block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.SIGN) || block.getType().equals(Material.WALL_SIGN) ){
						
						if( block.getType().equals(Material.AIR) ){
							block.setType(b.getSignType());
						}
						
						// Set the facing direction
						Sign s = (Sign)block.getState();
						
						if(block.getType().equals(Material.SIGN)){
							((org.bukkit.material.Sign)s.getData()).setFacingDirection(b.getFacing());
						}
						
						// Set content
						String[] lines = b.getLines();
						int i = 0;
						if(lines.length > 0){
							for(String line : lines){
								s.setLine(i, line);
								i++;
							}
						}
						s.update();
						changes_applied_count++;
					}
				}
			}
			
			// Apply deferred block changes
			for(Action a : deferredChanges){
				
				BlockAction b = (BlockAction) a;
				
				World world = plugin.getServer().getWorld(b.getWorld_name());
				Location loc = new Location(world, b.getX(), b.getY(), b.getZ());
				Block block = world.getBlockAt(loc);
				
				if(!is_preview){
					block.setTypeId( b.getBlock_id() );
					block.setData( b.getBlock_subid() );
				} else {
					player.sendBlockChange(block.getLocation(), b.getBlock_id(), b.getBlock_subid());
				}
				
				changes_applied_count++;
			}
			
			
			// POST ROLLBACK TRIGGERS
			if(processType.equals(PrismProcessType.ROLLBACK)){
			
				// We're going to modify the action type of the query params
				// and pass it along to a restore.
				// NOTE: These params have been modified from original, so
				// do NOT use the object for original params.
				
				/**
				 * If we've done breaking-blocks rollback we also need to re-apply
				 * any sign-change events at this location.
				 */
				if(parameters.shouldTriggerRestoreFor(ActionType.SIGN_CHANGE)){
					
					QueryParameters triggerParameters;
					try {
						triggerParameters = parameters.clone();
						triggerParameters.resetActionTypes();
						triggerParameters.addActionType(ActionType.SIGN_CHANGE);
						
						ActionsQuery aq = new ActionsQuery(plugin);
						QueryResult results = aq.lookup( player, triggerParameters );
						if(!results.getActionResults().isEmpty()){
							Restore rs = new Restore( plugin, player, results.getActionResults(), triggerParameters );
							rs.apply();
						}
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
				
				
				/**
				 * If we've rolled back any containers we need to restore item-removes.
				 */
				if(parameters.shouldTriggerRollbackFor(ActionType.ITEM_REMOVE)){
					
					plugin.debug("Action being rolled back triggers a second rollback: Item Remove");
					
					QueryParameters triggerParameters;
					try {
						triggerParameters = parameters.clone();
						triggerParameters.resetActionTypes();
						triggerParameters.addActionType(ActionType.ITEM_REMOVE);
						
						ActionsQuery aq = new ActionsQuery(plugin);
						QueryResult results = aq.lookup( player, triggerParameters );
						if(!results.getActionResults().isEmpty()){
							Rollback rb = new Rollback( plugin, player, results.getActionResults(), triggerParameters );
							rb.apply();
						}
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			// Make sure we move the player out of the way
			for(Player player : plugin.getServer().getWorld(parameters.getWorld()).getPlayers()){
				int add = 0;
				if(EntityUtils.inCube(parameters.getPlayerLocation(), parameters.getRadius(), player.getLocation())){
					Location l = player.getLocation();
					while( !EntityUtils.playerMayPassThrough(l.getBlock().getType()) ){
						add++;
						if(l.getY() >= 256) break;
						l.setY(l.getY() + 1);
					}
					if(add > 0){
						player.sendMessage(plugin.playerSubduedHeaderMsg("Moved you " + add + " blocks to safety due to a rollback."));
						player.teleport(l);
					}
				}
			}
			
			
			// Trigger the rollback event
			PrismBlocksRollbackEvent event = new PrismBlocksRollbackEvent(blockStateChanges, player.getName());
			plugin.getServer().getPluginManager().callEvent(event);
			
			return new ApplierResult( is_preview, changes_applied_count, skipped_block_count, blockStateChanges );
			
		}
		return null;
	}
}