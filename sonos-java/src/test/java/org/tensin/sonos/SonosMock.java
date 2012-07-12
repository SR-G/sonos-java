package org.tensin.sonos;

import java.util.Random;

import org.tensin.sonos.upnp.ISonosBrowseListener;
import org.tensin.sonos.upnp.SonosItem;
import org.tensin.sonos.upnp.XMLSequence;

/**
 * The Class SonosMock.
 */
public class SonosMock implements ISonos {

    /** The host. */
    private final String host;

    /**
     * Instantiates a new sonos mock.
     * 
     * @param host
     *            the host
     */
    public SonosMock(final String host) {
        this.host = host;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#add(java.lang.String)
     */
    @Override
    public void add(final String uri) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#browse(java.lang.String, org.tensin.sonos.upnp.ISonosBrowseListener)
     */
    @Override
    public void browse(final String _id, final ISonosBrowseListener cb) {
        try {
            if (cb != null) {
                int nb = 5 + new Random().nextInt(30);
                for (int i = 1; i <= nb; i++) {
                    SonosItem item = buildRandomItem();
                    cb.updateItem("", i, item);
                }
            }
        } finally {
            cb.updateDone(_id);
        }
    }

    @Override
    public void browseMetadata(final String _id, final ISonosBrowseListener cb) {

    }

    /**
     * Builds the random item.
     * 
     * @return the sonos item
     */
    private SonosItem buildRandomItem() {
        final String[] titles = new String[] { "(Viktoriya Yermolyeva) - 12. Consolation Part 2.mp3",
                "(Viktoriya Yermolyeva) - 04. Prokoffiev - Visions Fugitives Op 22 No 8.mp3", "(Viktoriya Yermolyeva) - 08. Bach - Italian Concerto.mp3",
                "(Viktoriya Yermolyeva) - 11. Liszt - Consolation.mp3", "(Viktoriya Yermolyeva) - 10. Rachmaninoff - Prelude D-Dur.mp3",
                "(Viktoriya Yermolyeva) - 02. Rachmaninoff - Prelude G-Dur.mp3", "(Viktoriya Yermolyeva) - 05. Chopin - Prelude.mp3",
                "(Viktoriya Yermolyeva) - 09. Ravel - Pavane for a Dead Infanta.mp3", "(Viktoriya Yermolyeva) - 07. Bach - Marchello.mp3",
                "(Viktoriya Yermolyeva) - 03. Consolation Part 1.mp3", "(Viktoriya Yermolyeva) - 14. Darkness.mp3", "(Viktoriya Yermolyeva) - 01. Light.mp3",
                "(Viktoriya Yermolyeva) - 13. Moody.mp3", "(Viktoriya Yermolyeva) - 06. Emptyness.mp3", "(Portishead) - 05. Wandering Star.mp3",
                "(Portishead) - 06. It's A Fire.mp3", "(Portishead) - 01. Mysterons.mp3", "(Portishead) - 11. Glory Box.mp3",
                "(Portishead) - 04. It Could Be Sweet.mp3", "(Portishead) - 03. Strangers.mp3", "(Portishead) - 07. Numb.mp3",
                "(Portishead) - 10. Biscuit.mp3", "(Portishead) - 02. Sour Times.mp3", "(Portishead) - 08. Roads.mp3", "(Portishead) - 09. Pedestal.mp3",
                "(Jucifer) - 02. Long Live the King.mp3", "(Jucifer) - 10. Hero Worship.mp3", "(Jucifer) - 07. Nickel to Roll.mp3",
                "(Jucifer) - 03. Superman.mp3", "(Jucifer) - 09. More Luminous Skin.mp3", "(Jucifer) - 13. Movements of Swallows.mp3",
                "(Jucifer) - 08. Glamourpuss.mp3", "(Jucifer) - 12. Model Year Blowout.mp3", "(Jucifer) - 05. To the Place.mp3",
                "(Jucifer) - 01. Code Escovedo.mp3", "(Jucifer) - 06. 44 Dying in White.mp3", "(Jucifer) - 14. japanese and Lovely.mp3",
                "(Jucifer) - 11. Ruin and Pink Chillon.mp3", "(Jucifer) - 04. Malibu.mp3", "(La Rue Kétanou) - 08. Elle est belle.mp3",
                "(La Rue Kétanou) - 02. Germaine.mp3", "(La Rue Kétanou) - 07. Les idées qui fument.mp3", "(La Rue Kétanou) - 13. Revenir du lointain.mp3",
                "(La Rue Kétanou) - 11. 80 tours de la terre.mp3", "(La Rue Kétanou) - 09. Se laisser embarquer.mp3", "(La Rue Kétanou) - 06. Ton cabaret.mp3",
                "(La Rue Kétanou) - 14. Les derniers aventuriers.mp3", "(La Rue Kétanou) - 10. Sao Paolo.mp3",
                "(La Rue Kétanou) - 12. Derrière ses cheveux longs.mp3", "(La Rue Kétanou) - 05. Prenons la vie.mp3",
                "(La Rue Kétanou) - 01. Todas las mujeres.mp3", "(La Rue Kétanou) - 04. Je peux pas te promettre.mp3",
                "(La Rue Kétanou) - 03. Maître Corbeau.mp3", "(Les Becs Bien Zen) - 07. Va.mp3", "(Les Becs Bien Zen) - 08. Nos montagnes.mp3",
                "(Les Becs Bien Zen) - 04. Debout!.mp3", "(Les Becs Bien Zen) - 01. Moulin joli.mp3", "(Les Becs Bien Zen) - 12. Délivré.mp3",
                "(Les Becs Bien Zen) - 03. Le diable au coeur.mp3", "(Les Becs Bien Zen) - 10. J'aurai ta peau.mp3",
                "(Les Becs Bien Zen) - 05. Esprit de masse.mp3", "(Les Becs Bien Zen) - 13. Grand feu.mp3",
                "(Les Becs Bien Zen) - 06. Sexe et rose n'roll.mp3", "(Les Becs Bien Zen) - 11. Ma reine.mp3",
                "(Les Becs Bien Zen) - 02. L'espace et le temps.mp3", "(Les Becs Bien Zen) - 09. Ami es-tu .mp3",
                "(A prendre ou à laisser) - 06. Franc-Comtois.mp3", "(A prendre ou à laisser) - 04. Rien.mp3", "(A prendre ou à laisser) - 05. Lison.mp3",
                "(A prendre ou à laisser) - 02. La complainte du facteur.mp3", "(A prendre ou à laisser) - 01. A prendre ou à laisser.mp3",
                "(A prendre ou à laisser) - 07. Petite pierre.mp3", "(A prendre ou à laisser) - 03. Les rois des l'arnaque.mp3",
                "(A prendre ou à laisser) - 08. A toi qui n'es pas encore né.mp3", "(Jucifer) - 02. Lambs, Pt. 2.mp3", "(Jucifer) - 06. White Devils.mp3",
                "(Jucifer) - 01. Lambs, Pt. 1.mp3", "(Jucifer) - 04. Lambs, Pt. 4.mp3", "(Jucifer) - 05. Platinum High.mp3",
                "(Jucifer) - 03. Lambs, Pt. 3.mp3", "(Jucifer) - 07. Gunsick.mp3", "(AaRON) - 10. A thousand wars.mp3", "(AaRON) - 01. Ludlow.mp3",
                "(AaRON) - 02. Rise.mp3", "(AaRON) - 07. Arm your eyes.mp3", "(AaRON) - 09. The lame souls.mp3", "(AaRON) - 08. Birds in the storm.mp3",
                "(AaRON) - 05. Inner streets.mp3", "(AaRON) - 03. Seeds of gold.mp3", "(AaRON) - 06. Song for ever.mp3",
                "(AaRON) - 04. Waiting for the wind to come.mp3", "(AaRON) - 12. Embers.mp3", "(AaRON) - 11. Passengers.mp3",
                "(Interpol) - 01. Pioneer To the Falls.mp3", "(Interpol) - 04. The Heinrich Maneuver.mp3", "(Interpol) - 08. Rest My Chemistry.mp3",
                "(Interpol) - 07. All Fired Up.mp3", "(Interpol) - 05. Mammoth.mp3", "(Interpol) - 02. No I In Threesome.mp3",
                "(Interpol) - 11. The Lighthouse.mp3", "(Interpol) - 06. Pace Is the Trick.mp3", "(Interpol) - 03. The Scale.mp3",
                "(Interpol) - 09. Who Do You Think.mp3", "(Interpol) - 10. Wrecking Ball.mp3", "(Heligoland) - 01. Wandering Wide Awake.mp3",
                "(Heligoland) - 02. The Road to Alas.mp3", "(Heligoland) - 04. Castillon.mp3", "(Heligoland) - 03. Swallows Return.mp3",
                "(Heligoland) - 05. July 14.mp3", "(Viktoriya Yermolyeva) - Cigarette Break (improvisation).mp3", "(Viktoriya Yermolyeva) - Rabbit.mp3",
                "(Viktoriya Yermolyeva) - Tam Tadam.mp3", "(Viktoriya Yermolyeva) - Another very typical sad improvisation.mp3",
                "(Viktoriya Yermolyeva) - In between indian movie and falling asleep.mp3",
                "(Viktoriya Yermolyeva) - Raindrops up your ass (improvisation).mp3", "(Viktoriya Yermolyeva) - In A Sunday Night Mood.mp3",
                "(Viktoriya Yermolyeva) - My song.mp3", "(Viktoriya Yermolyeva) - The Usual Stuff with some Drama.mp3",
                "(Viktoriya Yermolyeva) - Weird Bluesy Tune.mp3", "(Viktoriya Yermolyeva) - Envy for Jazzmen.mp3",
                "(Viktoriya Yermolyeva) - Lullaby For a Weird Pet.mp3", "(Viktoriya Yermolyeva) - Dutch Beauty (improvisation).mp3",
                "(Viktoriya Yermolyeva) - Light.mp3", "(Viktoriya Yermolyeva) - Tragical Incident With A Rabbit.mp3",
                "(Viktoriya Yermolyeva) - November 28 (improvisation).mp3", "(Viktoriya Yermolyeva) - Very, very, VERY sad improvisation.mp3",
                "(Viktoriya Yermolyeva) - Soundcheck (improvisation in NIN style).mp3", "(Viktoriya Yermolyeva) - Painkillers (improvisation).mp3",
                "(Viktoriya Yermolyeva) - Piano improvisation (traditional style).mp3", "(Viktoriya Yermolyeva) - A.S.I. (Another Sad Improvisation) II.mp3",
                "(Viktoriya Yermolyeva) - June 9 (improvisation).mp3", "(Viktoriya Yermolyeva) - Good Bye.mp3",
                "(Viktoriya Yermolyeva) - Psychedelic Christmas.mp3", "(Heligoland) - 05. Nearness.mp3", "(Heligoland) - 08. Corazu00f3n.mp3",
                "(Heligoland) - 01. Kiss Kiss Bang Bang.mp3", "(Heligoland) - 06. Your Longest Breath.mp3", "(Heligoland) - 03. Mapping Your Desires.mp3",
                "(Heligoland) - 04. A Year Without Sunlight.mp3", "(Heligoland) - 02. The Light Inside.mp3", "(Heligoland) - 07. All Your Ships Are White.mp3",
                "(Jucifer) - 15. Led.mp3", "(Jucifer) - 08. Pontius Of Palia.mp3", "(Jucifer) - 01. She Tides The Deep.mp3",
                "(Jucifer) - 06. My Benefactor.mp3", "(Jucifer) - 12. The Plastic Museum.mp3", "(Jucifer) - 10. Luchamos.mp3",
                "(Jucifer) - 13. In A Family Way.mp3", "(Jucifer) - 04. Hennin Hardine.mp3", "(Jucifer) - 11. Ludlow.mp3",
                "(Jucifer) - 03. Lucky Ones Burn.mp3", "(Jucifer) - 09. Backslider.mp3", "(Jucifer) - 02. Centralia.mp3", "(Jucifer) - 07. Four Sons.mp3",
                "(Jucifer) - 05. Antietam.mp3", "(Jucifer) - 14. Medicated.mp3", "(Jucifer) - 13. Traitors.mp3", "(Jucifer) - 16. The Mountain.mp3",
                "(Jucifer) - 04. Deficit.mp3", "(Jucifer) - 17. Window (Where The Sea Falls Forever).mp3", "(Jucifer) - 01. Blackpowder.mp3",
                "(Jucifer) - 11. October.mp3", "(Jucifer) - 07. To The End.mp3", "(Jucifer) - 20. Coma.mp3", "(Jucifer) - 15. Noyade.mp3",
                "(Jucifer) - 05. Champ De Mars.mp3", "(Jucifer) - 09. L'autrichienne.mp3", "(Jucifer) - 06. Fall Of The Bastille.mp3",
                "(Jucifer) - 14. The Law Of Suspects.mp3", "(Jucifer) - 21. The Assembly.mp3", "(Jucifer) - 10. Behind Every Great Man.mp3",
                "(Jucifer) - 19. Procession A La Guillotine.mp3", "(Jucifer) - 18. Fleur De Lis.mp3", "(Jucifer) - 12. Birds Of A Feather.mp3",
                "(Jucifer) - 08. Armada.mp3", "(Jucifer) - 02. Thermidor.mp3", "(Jucifer) - 03. To Earth.mp3", "(Camille Bazbaz) - 06. Papa Tango Charly.mp3",
                "(Camille Bazbaz) - 05. Tout pour l'éviter.mp3", "(Camille Bazbaz) - 01. Infinie solitude.mp3", "(Camille Bazbaz) - 07. Le crocodile.mp3",
                "(Camille Bazbaz) - 10. Le professionnel.mp3", "(Camille Bazbaz) - 04. Souviens toi.mp3", "(Camille Bazbaz) - 11. Psychologie féminine.mp3",
                "(Camille Bazbaz) - 03. Sur le bout de la langue.mp3", "(Camille Bazbaz) - 08. Fatale.mp3", "(Camille Bazbaz) - 02. Tutto va bene.mp3",
                "(Camille Bazbaz) - 09. Dans ma nature.mp3", "(Viktoriya Yermolyeva) - Nothing Else Matters.mp3",
                "(Viktoriya Yermolyeva) - Fight Fire With Fire.mp3", "(Viktoriya Yermolyeva) - Rotten Apple.mp3", "(Viktoriya Yermolyeva) - Give In To Me.mp3",
                "(Viktoriya Yermolyeva) - Not So Pretty Now.mp3", "(Viktoriya Yermolyeva) - The Dragonfighter.mp3",
                "(Viktoriya Yermolyeva) - More Then A Feeling.mp3", "(Viktoriya Yermolyeva) - Danger Keep Away.mp3", "(Viktoriya Yermolyeva) - To Sheila.mp3",
                "(Viktoriya Yermolyeva) - Ashes To Ashes.mp3", "(Viktoriya Yermolyeva) - Porcelain Heart.mp3", "(Viktoriya Yermolyeva) - Te Quiero Puta.mp3",
                "(Viktoriya Yermolyeva) - Symphony of Destruction.mp3", "(Viktoriya Yermolyeva) - Push and Pull.mp3",
                "(Viktoriya Yermolyeva) - Ecstasy Of Gold.mp3", "(Viktoriya Yermolyeva) - Estranged.mp3", "(Viktoriya Yermolyeva) - Non Entity.mp3",
                "(Viktoriya Yermolyeva) - Harvester Of Sorrow.mp3", "(Viktoriya Yermolyeva) - The Day That Never Comes.mp3",
                "(Viktoriya Yermolyeva) - The Day The World Went Away.mp3", "(Viktoriya Yermolyeva) - Fear Of A Blank Planet.mp3",
                "(Viktoriya Yermolyeva) - Ride the Lightning.mp3", "(Viktoriya Yermolyeva) - Hero Of The Day.mp3",
                "(Viktoriya Yermolyeva) - Anarchy In The U.K..mp3", "(Viktoriya Yermolyeva) - Street of Dreams.mp3",
                "(Viktoriya Yermolyeva) - To Live Is to Die.mp3", "(Viktoriya Yermolyeva) - Echoes.mp3", "(Viktoriya Yermolyeva) - Dyers Eve.mp3",
                "(Viktoriya Yermolyeva) - Screen Man.mp3", "(Viktoriya Yermolyeva) - One Rainy Day.mp3", "(Viktoriya Yermolyeva) - Invisible Wounds.mp3",
                "(Viktoriya Yermolyeva) - This Woman's Work.mp3", "(Viktoriya Yermolyeva) - The Outlaw Torn.mp3", "(Viktoriya Yermolyeva) - Sober.mp3",
                "(Viktoriya Yermolyeva) - Seek And Destroy.mp3", "(Viktoriya Yermolyeva) - No Ordinary Love.mp3", "(Viktoriya Yermolyeva) - Fuel.mp3",
                "(Viktoriya Yermolyeva) - Suicide And Redemption.mp3", "(Viktoriya Yermolyeva) - Tijuana Lady.mp3",
                "(Viktoriya Yermolyeva) - Until It Sleeps.mp3", "(Viktoriya Yermolyeva) - Sweet Child o' Mine.mp3",
                "(Viktoriya Yermolyeva) - Love You To Death.mp3", "(Viktoriya Yermolyeva) - Forest.mp3", "(Viktoriya Yermolyeva) - Nude.mp3",
                "(Viktoriya Yermolyeva) - Sign Of The Cross.mp3", "(Viktoriya Yermolyeva) - Full Circle.mp3",
                "(Viktoriya Yermolyeva) - She's Lost Control.mp3", "(Viktoriya Yermolyeva) - Poor Twisted Me.mp3", "(Viktoriya Yermolyeva) - Simple Man.mp3",
                "(Viktoriya Yermolyeva) - I Wanna Be Your Dog.mp3", "(Viktoriya Yermolyeva) - Outside.mp3",
                "(Viktoriya Yermolyeva) - Running Up That Hill.mp3", "(Viktoriya Yermolyeva) - A Tout Le Monde.mp3",
                "(Viktoriya Yermolyeva) - To Bid You Farewell.mp3", "(Viktoriya Yermolyeva) - The Show Must Go On.mp3",
                "(Viktoriya Yermolyeva) - Cherub Rock.mp3", "(Viktoriya Yermolyeva) - Snuff.mp3", "(Viktoriya Yermolyeva) - Astronomy.mp3",
                "(Viktoriya Yermolyeva) - The Four Horsemen.mp3", "(Viktoriya Yermolyeva) - The Eternal.mp3",
                "(Viktoriya Yermolyeva) - Supermassive Black Hole.mp3", "(Viktoriya Yermolyeva) - Burn.mp3", "(Viktoriya Yermolyeva) - Use Somebody.mp3",
                "(Viktoriya Yermolyeva) - 3 Libras.mp3", "(Viktoriya Yermolyeva) - Somebody.mp3", "(Viktoriya Yermolyeva) - November Rain.mp3",
                "(Viktoriya Yermolyeva) - Hey Man Nice Shot.mp3", "(Viktoriya Yermolyeva) - Raining Blood (Slayer).mp3",
                "(Viktoriya Yermolyeva) - For Whom The Bell Tolls.mp3", "(Viktoriya Yermolyeva) - Somebody To Love.mp3",
                "(Viktoriya Yermolyeva) - Cemetery Gates.mp3", "(Viktoriya Yermolyeva) - Wait For Sleep.mp3", "(Viktoriya Yermolyeva) - This Love.mp3",
                "(Viktoriya Yermolyeva) - Bohemian Rhapsody.mp3", "(Viktoriya Yermolyeva) - Disposable Heroes.mp3",
                "(Viktoriya Yermolyeva) - Streets Of Love.mp3", "(Viktoriya Yermolyeva) - Disarm.mp3", "(Viktoriya Yermolyeva) - We Are In This Together.mp3",
                "(Viktoriya Yermolyeva) - Turn The Page.mp3", "(Viktoriya Yermolyeva) - Seasons In the Abyss.mp3", "(Viktoriya Yermolyeva) - Le Moulin.mp3",
                "(Viktoriya Yermolyeva) - Wherever I May Roam.mp3", "(Viktoriya Yermolyeva) - Hysteria.mp3", "(Viktoriya Yermolyeva) - The Great Below.mp3",
                "(Viktoriya Yermolyeva) - Low Man's Lyric.mp3", "(Viktoriya Yermolyeva) - Blind.mp3", "(Viktoriya Yermolyeva) - Wrong.mp3",
                "(Viktoriya Yermolyeva) - Stuck In Here.mp3", "(Viktoriya Yermolyeva) - Lights In The Sky.mp3", "(Viktoriya Yermolyeva) - I'm The One.mp3",
                "(Viktoriya Yermolyeva) - Right Where It Belongs.mp3", "(Viktoriya Yermolyeva) - Ace Of Spades.mp3", "(Viktoriya Yermolyeva) - ATWA.mp3",
                "(Viktoriya Yermolyeva) - Missing.mp3", "(Viktoriya Yermolyeva) - Mama Said.mp3", "(Viktoriya Yermolyeva) - Inside Out.mp3",
                "(Viktoriya Yermolyeva) - Frogs.mp3", "(Viktoriya Yermolyeva) - BreГ±a.mp3", "(Viktoriya Yermolyeva) - Special Needs.mp3",
                "(Viktoriya Yermolyeva) - Beggar's Prayer.mp3", "(Viktoriya Yermolyeva) - The Unforgiven.mp3", "(Viktoriya Yermolyeva) - South Of Heaven.mp3",
                "(Viktoriya Yermolyeva) - Discipline.mp3", "(Viktoriya Yermolyeva) - The Waves Of The Caspian Sea.mp3",
                "(Viktoriya Yermolyeva) - 1 Ghost I.mp3", "(Viktoriya Yermolyeva) - This I Love.mp3",
                "(Viktoriya Yermolyeva) - Creep (Stone Temple Pilots).mp3", "(Viktoriya Yermolyeva) - Vermillion Part 2.mp3",
                "(Viktoriya Yermolyeva) - War Pigs.mp3", "(Viktoriya Yermolyeva) - To Forgive.mp3", "(Viktoriya Yermolyeva) - Change.mp3",
                "(Viktoriya Yermolyeva) - Sad But True.mp3", "(Viktoriya Yermolyeva) - Learn From This Mistake.mp3",
                "(Viktoriya Yermolyeva) - Creeping Death.mp3", "(Viktoriya Yermolyeva) - Holy Diver.mp3", "(Viktoriya Yermolyeva) - Enter Sandman.mp3",
                "(Viktoriya Yermolyeva) - La Valse d'Amelie.mp3", "(Viktoriya Yermolyeva) - Adrift And At Peace.mp3", "(Viktoriya Yermolyeva) - Apologize.mp3",
                "(Viktoriya Yermolyeva) - And All That Could Have Been.mp3", "(Viktoriya Yermolyeva) - No Leaf Clover.mp3",
                "(Viktoriya Yermolyeva) - Oblivion.mp3", "(Viktoriya Yermolyeva) - Davidian.mp3", "(Viktoriya Yermolyeva) - All The Love In The World.mp3",
                "(Viktoriya Yermolyeva) - Comptine D'un Autre ete l'apres Midi.mp3", "(Viktoriya Yermolyeva) - The Frail.mp3",
                "(Viktoriya Yermolyeva) - Goodbye Lament.mp3", "(Viktoriya Yermolyeva) - Creep.mp3", "(Viktoriya Yermolyeva) - Truenorth part 2.mp3",
                "(Viktoriya Yermolyeva) - Battery.mp3", "(Viktoriya Yermolyeva) - Butterflies and Hurricanes.mp3",
                "(Viktoriya Yermolyeva) - March Of The Pigs.mp3", "(Viktoriya Yermolyeva) - Battery version 2.mp3", "(Viktoriya Yermolyeva) - Lazy.mp3",
                "(Viktoriya Yermolyeva) - Gabriel.mp3", "(Viktoriya Yermolyeva) - Not An Addict.mp3", "(Viktoriya Yermolyeva) - Nutshell.mp3",
                "(Viktoriya Yermolyeva) - Hurt version 2.mp3", "(Viktoriya Yermolyeva) - Sweet Leaf.mp3", "(Viktoriya Yermolyeva) - Hurt.mp3",
                "(Viktoriya Yermolyeva) - Patterns In The Ivy.mp3", "(Viktoriya Yermolyeva) - Deadsong.mp3", "(Viktoriya Yermolyeva) - Winning.mp3",
                "(Viktoriya Yermolyeva) - Cowboys From Hell.mp3", "(Viktoriya Yermolyeva) - Frozen.mp3", "(Viktoriya Yermolyeva) - To Have And To Hold.mp3",
                "(Viktoriya Yermolyeva) - Orion.mp3", "(Viktoriya Yermolyeva) - Raining Blood.mp3", "(Viktoriya Yermolyeva) - Symphony No5.mp3",
                "(Viktoriya Yermolyeva) - Black Gives Way To Blue.mp3", "(Viktoriya Yermolyeva) - The Call Of Ktulu.mp3",
                "(Viktoriya Yermolyeva) - Dance Of Death.mp3", "(Viktoriya Yermolyeva) - All Nightmare Long.mp3", "(Viktoriya Yermolyeva) - One.mp3",
                "(Viktoriya Yermolyeva) - River Of Deceit.mp3", "(Viktoriya Yermolyeva) - Sorry.mp3", "(Viktoriya Yermolyeva) - Warm Place.mp3",
                "(Viktoriya Yermolyeva) - Momma Sed.mp3", "(Viktoriya Yermolyeva) - If The World.mp3", "(Viktoriya Yermolyeva) - Fear Of The Dark.mp3",
                "(Viktoriya Yermolyeva) - Evidence.mp3", "(Viktoriya Yermolyeva) - Planet Caravan.mp3", "(Viktoriya Yermolyeva) - Spiders.mp3",
                "(Viktoriya Yermolyeva) - Hello.mp3", "(Viktoriya Yermolyeva) - La Dispute.mp3", "(Viktoriya Yermolyeva) - The Unforgiven II.mp3",
                "(Viktoriya Yermolyeva) - The Call Of Ktulu 2nd version.mp3", "(Viktoriya Yermolyeva) - Only The Names.mp3",
                "(Viktoriya Yermolyeva) - Personal Jesus.mp3", "(Viktoriya Yermolyeva) - Lullaby.mp3", "(Viktoriya Yermolyeva) - Master Of Puppets.mp3",
                "(Viktoriya Yermolyeva) - ... and Justice for All.mp3", "(Viktoriya Yermolyeva) - Bleeding Me.mp3",
                "(Viktoriya Yermolyeva) - Night of the Lotus Eaters.mp3", "(Viktoriya Yermolyeva) - Mechanix.mp3",
                "(Viktoriya Yermolyeva) - Save Your Day.mp3", "(Viktoriya Yermolyeva) - Hold On.mp3", "(Viktoriya Yermolyeva) - Twenty Four Hours.mp3",
                "(Viktoriya Yermolyeva) - School.mp3", "(Viktoriya Yermolyeva) - Aesthetics of Hate.mp3", "(Viktoriya Yermolyeva) - Orion (Metallica).mp3",
                "(Viktoriya Yermolyeva) - Exogenesis Symphony Pt 1 Overture.mp3", "(Viktoriya Yermolyeva) - Something I Can Never Have.mp3",
                "(Viktoriya Yermolyeva) - Fade To Black.mp3", "(Viktoriya Yermolyeva) - Something In The Way.mp3", "(Viktoriya Yermolyeva) - Toxicity.mp3",
                "(Viktoriya Yermolyeva) - Deliverance.mp3", "(Viktoriya Yermolyeva) - Running Blind.mp3", "(Viktoriya Yermolyeva) - Hope Leaves.mp3",
                "(Viktoriya Yermolyeva) - Heart Shaped Box.mp3", "(Viktoriya Yermolyeva) - Terrible Lie.mp3",
                "(Viktoriya Yermolyeva) - Wish You Were Here.mp3", "(Ina-Ich) - 10. Au revoir.mp3", "(Ina-Ich) - 05. Belle Asiatique.mp3",
                "(Ina-Ich) - 07. Parfait.mp3", "(Ina-Ich) - 04. Le Train.mp3", "(Ina-Ich) - 03. Libre comme l'eau.mp3", "(Ina-Ich) - 08. Mon Empire.mp3",
                "(Ina-Ich) - 01. Ame armée.mp3", "(Ina-Ich) - 12. Sale Crapaud.mp3", "(Ina-Ich) - 06. Crache.mp3", "(Ina-Ich) - 11. Belle O Scalpel.mp3",
                "(Ina-Ich) - 09. Aime-moi.mp3", "(Ina-Ich) - 02. Seul.mp3", "(Jucifer) - 15. Sea Blind.mp3", "(Jucifer) - 02. Amplifier.mp3",
                "(Jucifer) - 13. Surface Tension.mp3", "(Jucifer) - 03. Pinned In Glass.mp3", "(Jucifer) - 01. Little Fever.mp3",
                "(Jucifer) - 08. Fight Song.mp3", "(Jucifer) - 05. When She Goes Out.mp3", "(Jucifer) - 06. Torch.mp3", "(Jucifer) - 09. Dissolver.mp3",
                "(Jucifer) - 12. Lazing.mp3", "(Jucifer) - 11. Firefly.mp3", "(Jucifer) - 10. Vulture Story.mp3", "(Jucifer) - 07. Memphis.mp3",
                "(Jucifer) - 04. Queen B.mp3", "(Jucifer) - 14. Undertow.mp3", "(Mademoiselle K) - 08. Click clock.mp3",
                "(Mademoiselle K) - 10. Alors je dessine.mp3", "(Mademoiselle K) - 11. Enjoliveur.mp3", "(Mademoiselle K) - 06. Pas des carrés.mp3",
                "(Mademoiselle K) - 04. Maman XY.mp3", "(Mademoiselle K) - 09. Tea time.mp3", "(Mademoiselle K) - 03. Jamais la paix.mp3",
                "(Mademoiselle K) - 12. Espace.mp3", "(Mademoiselle K) - 01. Le vent la fureur.mp3", "(Mademoiselle K) - 02. A.S.D..mp3",
                "(Mademoiselle K) - 05. Grave.mp3", "(Mademoiselle K) - 07. En smoking.mp3", "(Cake) - 10. It's Coming Down.mp3",
                "(Cake) - 09. Perhaps, Perhaps, Perhaps.mp3", "(Cake) - 07. I Will Survive.mp3", "(Cake) - 03. Friend Is A Four Letter Word.mp3",
                "(Cake) - 04. Open Book.mp3", "(Cake) - 05. Daria.mp3", "(Cake) - 08.Stickshifts And Safetybelts.mp3", "(Cake) - 11. Nugget.mp3",
                "(Cake) - 12. She'll Come back To Me.mp3", "(Cake) - 02. The Distance.mp3", "(Cake) - 06. Race Car Ya-Yas.mp3",
                "(Cake) - 13. Italian Leather Sofa.mp3", "(Cake) - 14. Sad Songs And Waltzes.mp3", "(Cake) - 01. Frank Sinatra.mp3",
                "(Kills) - 02. Love Is A Deserter.mp3", "(Kills) - 08. Sweet Cloud.mp3", "(Kills) - 10. Murdermile.mp3",
                "(Kills) - 06. I Hate The Way You Love Part 2.mp3", "(Kills) - 11. Ticket Man.mp3", "(Kills) - 05. I Hate The Way You Love.mp3",
                "(Kills) - 03. Dead Road 7.mp3", "(Kills) - 01. No Wow.mp3", "(Kills) - 04. The Good Ones.mp3", "(Kills) - 09. Rodeo Town.mp3",
                "(Kills) - 07. At The Back Of The Shell.mp3", "(Charlie Winston) - 12. My Name.mp3", "(Charlie Winston) - 04. I Love Your Smile.mp3",
                "(Charlie Winston) - 07. Calling Me.mp3", "(Charlie Winston) - 11. Every Step.mp3", "(Charlie Winston) - 06. Boxes.mp3",
                "(Charlie Winston) - 08. Tongue Tied.mp3", "(Charlie Winston) - 05. My Life As A Duck.mp3", "(Charlie Winston) - 01. In Your Hands.mp3",
                "(Charlie Winston) - 02. Like A Hobo.mp3", "(Charlie Winston) - 10. Generation Spent.mp3", "(Charlie Winston) - 03. Kick The Bucket.mp3",
                "(Charlie Winston) - 09. Soundtrack To Falling In Love.mp3", "(Shaka Ponk) - 11. Spit low.mp3", "(Shaka Ponk) - 09. Da teen.mp3",
                "(Shaka Ponk) - 10. Hello.mp3", "(Shaka Ponk) - 07. Watch Ha.mp3", "(Shaka Ponk) - 05. Body Cult.mp3", "(Shaka Ponk) - 03. Skid 1.mp3",
                "(Shaka Ponk) - 13. Fonk Me.mp3", "(Shaka Ponk) - 01. Lama laico.mp3", "(Shaka Ponk) - 02. Tekkno Kills.mp3",
                "(Shaka Ponk) - 08. Dot coma.mp3", "(Shaka Ponk) - 12. Skid 2.mp3", "(Shaka Ponk) - 14. Popa.mp3", "(Shaka Ponk) - 15. Spit.mp3",
                "(Shaka Ponk) - 06. My Boom.mp3", "(Shaka Ponk) - 16. Sonic.mp3", "(Shaka Ponk) - 04. Disto Cake.mp3", "(Portishead) - 01. Silence.mp3",
                "(Portishead) - 11. Threads.mp3", "(Portishead) - 03. Nylon Smile.mp3", "(Portishead) - 07. Deep Water.mp3",
                "(Portishead) - 08. Machine Gun.mp3", "(Portishead) - 02. Hunter.mp3", "(Portishead) - 04. The Rip.mp3", "(Portishead) - 05. Plastic.mp3",
                "(Portishead) - 09. Small.mp3", "(Portishead) - 06. We Carry On.mp3", "(Portishead) - 10. Magic Doors.mp3", "(AaRON) - 07. Strange Fruit.mp3",
                "(AaRON) - 13. Little Love.mp3", "(AaRON) - 09. War Flag.mp3", "(AaRON) - 11. Le Tunnel D'or.mp3", "(AaRON) - 04. Mister K.mp3",
                "(AaRON) - 01. Endless Song.mp3", "(AaRON) - 06. Beautiful Scar.mp3", "(AaRON) - 02. U Turn (Lili).mp3", "(AaRON) - 08. Angel Dust.mp3",
                "(AaRON) - 10. Lost Highway.mp3", "(AaRON) - 03. O Song.mp3", "(AaRON) - 05. Blow.mp3", "(AaRON) - 12. Piste 12.mp3",
                "(Lizz Wright) - 01. Coming Home.mp3", "(Lizz Wright) - 12. Strange.mp3", "(Lizz Wright) - 03. Idolize You.mp3",
                "(Lizz Wright) - 09. This Is.mp3", "(Lizz Wright) - 04. Hey Mann.mp3", "(Lizz Wright) - 10. Song For Mia.mp3",
                "(Lizz Wright) - 05. Another Angel.mp3", "(Lizz Wright) - 02. My Heart.mp3", "(Lizz Wright) - 08. Speak Your Heart.mp3",
                "(Lizz Wright) - 06. When I Fall.mp3", "(Lizz Wright) - 13. It Makes No Difference.mp3", "(Lizz Wright) - 07. Leave Me Standing Alone.mp3",
                "(Lizz Wright) - 11. Thank You.mp3", "(Saez) - 06. J'accuse.mp3", "(Saez) - 14. Tricycle Jaune.mp3", "(Saez) - 13. On A Tous Une Lula.mp3",
                "(Saez) - 03. Cigarette.mp3", "(Saez) - 05. Sonnez Tocsin Dans Les Campagnes.mp3", "(Saez) - 11. Les Printemps.mp3",
                "(Saez) - 04. Des P'tits Sous.mp3", "(Saez) - 07. Lula.mp3", "(Saez) - 12. Marguerite.mp3", "(Saez) - 02. Pilule.mp3",
                "(Saez) - 09. Regarder Les Filles Pleurer Thème.mp3", "(Saez) - 08. Regarder Les Filles Pleurer.mp3", "(Saez) - 01. Les Anarchitectures.mp3",
                "(Saez) - 10. Les Cours Des Lycées.mp3", "(HorrorPops) - 06. Everything's Everything.mp3", "(HorrorPops) - 04. Heading for the Disco.mp3",
                "(HorrorPops) - 01. Thelma & Louise.mp3", "(HorrorPops) - 03. Boot2Boot.mp3", "(HorrorPops) - 09. HorrorBeach Pt. 2 [Instrumental].mp3",
                "(HorrorPops) - 12. Private Hall of Shame.mp3", "(HorrorPops) - 08. Highway55.mp3", "(HorrorPops) - 07. Hitchcock Starlet.mp3",
                "(HorrorPops) - 11. Keep My Picture!.mp3", "(HorrorPops) - 02. MissFit.mp3", "(HorrorPops) - 05. Kiss Kiss Kill Kill.mp3",
                "(HorrorPops) - 10. Copenhagen Refugee.mp3", "(Manu) - 01. Allée des tilleuls.mp3", "(Manu) - 08. Dis-moi un secret.mp3",
                "(Manu) - 03. Oh my friend.mp3", "(Manu) - 11. Goodbye.mp3", "(Manu) - 05. Un beau jour.mp3", "(Manu) - 09. T'es bo, t'es con.mp3",
                "(Manu) - 10. Suteki ni.mp3", "(Manu) - 12. Rendez-vous.mp3", "(Manu) - 07. Sur mes levres.mp3", "(Manu) - 06. Cow-boy.mp3",
                "(Manu) - 02. Tes cicatrices.mp3", "(Manu) - 04. Dans les yeux.mp3", "(Shaka Ponk) - 07. Do.mp3", "(Shaka Ponk) - 11. Som luv.mp3",
                "(Shaka Ponk) - 01. Twisted mind.mp3", "(Shaka Ponk) - 06. Mad O You.mp3", "(Shaka Ponk) - 12. Alak Okan.mp3",
                "(Shaka Ponk) - 05. How we kill stars.mp3", "(Shaka Ponk) - 09. French touch puta madre.mp3", "(Shaka Ponk) - 14. Just a nerd.mp3",
                "(Shaka Ponk) - 02. El hombre que soy.mp3", "(Shaka Ponk) - 13. Make it mine.mp3", "(Shaka Ponk) - 03. Prima scene.mp3",
                "(Shaka Ponk) - 08. Te gusta me.mp3", "(Shaka Ponk) - 10. Gotta get me high.mp3", "(Shaka Ponk) - 04. Some guide.mp3",
                "03 - Tahrire zangue shotor.mp3", "05 - Souqi name.mp3", "02 - suite azeri.mp3", "06 - Masnavi.mp3",
                "08 - Tchaharmezrab de Saed Farajpouri.mp3", "04 - Ilk Bahar.mp3", "07 - Dotike.mp3", "01 - Daramad-e Esfehan.mp3",
                "(No One Is Innocent) - 05. Henry, serial killer.mp3", "(No One Is Innocent) - 02. Genocide.mp3", "(No One Is Innocent) - 07. Le feu.mp3",
                "(No One Is Innocent) - 04. Epargne moi.mp3", "(No One Is Innocent) - 06. Rusted faces.mp3",
                "(No One Is Innocent) - 03. They learn your love.mp3", "(No One Is Innocent) - 08. Ne reste-t-il que la guerre pour tuer le silence .mp3",
                "(No One Is Innocent) - 10. Beast in the bottle.mp3", "(No One Is Innocent) - 12. Gratitude.mp3", "(No One Is Innocent) - 11. Nova.mp3",
                "(No One Is Innocent) - 09. Another land.mp3", "(No One Is Innocent) - 01. La peau.mp3",
                "(Jucifer) - 02. Day Breaks on the Field of Battle.mp3", "(Jucifer) - 04. Haute Couture.mp3", "(Jucifer) - 05. The Shape of Texas.mp3",
                "(Jucifer) - 07. Silent Southern Summer Sunset.mp3", "(Jucifer) - 03. Seth.mp3", "(Jucifer) - 06. My Stars.mp3",
                "(Jucifer) - 01. Ideas of Light.mp3", "(Mademoiselle K) - 03. Ça me vexe.mp3", "(Mademoiselle K) - 05. Crève.mp3",
                "(Mademoiselle K) - 02. Ça sent l'été.mp3", "(Mademoiselle K) - 08. Fringue par fringue.mp3", "(Mademoiselle K) - 12. Final.mp3",
                "(Mademoiselle K) - 11. Plus le coeur à ça.mp3", "(Mademoiselle K) - 06. Grimper tout là haut.mp3", "(Mademoiselle K) - 01. Reste là.mp3",
                "(Mademoiselle K) - 04. Le cul entre deux chaises.mp3", "(Mademoiselle K) - 10. A côté.mp3", "(Mademoiselle K) - 09. A l'ombre.mp3",
                "(Mademoiselle K) - 07. Jalouse.mp3", "(HorrorPops) - 09. Walk Like A Zombie.mp3", "(HorrorPops) - 08. Trapped.mp3",
                "(HorrorPops) - 12. S.O.B..mp3", "(HorrorPops) - 10. Where You Can't Follow.mp3", "(HorrorPops) - 06. You Vs. Me.mp3",
                "(HorrorPops) - 02. Hit 'N Run.mp3", "(HorrorPops) - 04. It's Been So Long.mp3", "(HorrorPops) - 07. Crawl Straight Home.mp3",
                "(HorrorPops) - 13. Who's Leading You Now.mp3", "(HorrorPops) - 03. Bring It On!.mp3", "(HorrorPops) - 11. Caught In A Blonde.mp3",
                "(HorrorPops) - 01. Freaks In Uniforms.mp3", "(HorrorPops) - 05. Undefeated.mp3", "(Muse) - 06. Invincible.mp3", "(Muse) - 01. Take A Bow.mp3",
                "(Muse) - 12. Glorious.mp3", "(Muse) - 08. Exo-politics.mp3", "(Muse) - 04. Map of the Problematique.mp3", "(Muse) - 02. Starlight.mp3",
                "(Muse) - 03. Supermassive Black Hole.mp3", "(Muse) - 07. Assassin.mp3", "(Muse) - 11. Knights of Cydonia.mp3", "(Muse) - 10. Hoodoo.mp3",
                "(Muse) - 05. Soldier's Poem.mp3", "(Muse) - 09. City of Delusion.mp3", "(Portishead) - 04. Airbus Reconstruction.mp3",
                "(Portishead) - 03. Sheared Times.mp3", "(Portishead) - 05. Theme From To Kill A Dead Man.mp3", "(Portishead) - 02. Lot More.mp3",
                "(Portishead) - 01. Sour Sour Times.mp3", "(Portishead) - 01. Glory Box (Edit).mp3", "(Portishead) - 04. Sheared Box.mp3",
                "(Portishead) - 02. Glory Box (Mudflap Mix).mp3", "(Portishead) - 03. Scorn.mp3", "(Portishead) - 05. Toy Box.mp3",
                "(Jucifer) - 01. Throned In Blood.mp3", "(Jucifer) - 06. Hiroshima.mp3", "(Jucifer) - 03. Work Will Make Us Free.mp3",
                "(Jucifer) - 05. Disciples Of An Expanding Sun.mp3", "(Jucifer) - 02. Contempt.mp3", "(Jucifer) - 04. Return Of The Native.mp3",
                "(Jucifer) - 09. Spoils To The Conqueror.mp3", "(Jucifer) - 10. Armaggedon.mp3", "(Jucifer) - 07. Rifles.mp3",
                "(Jucifer) - 08. Good Provider.mp3" };
        Random random = new Random();
        int r = random.nextInt(titles.length);

        SonosItem result = new SonosItem();
        result.album = new XMLSequence();
        result.album.init("ALBUM".toCharArray(), 0, 5);
        result.title = new XMLSequence();
        result.title.init(titles[r].toCharArray(), 0, titles[r].length());
        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#destroy(java.lang.String)
     */
    @Override
    public void destroy(final String id) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#getPosition()
     */
    @Override
    public void getPosition() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#getTransportURI()
     */
    @Override
    public String getTransportURI() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#getZoneName()
     */
    @Override
    public String getZoneName() {
        return host;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#linein(java.lang.String)
     */
    @Override
    public void linein(final String line) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#move(int, int)
     */
    @Override
    public void move(final int from, final int to) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#next()
     */
    @Override
    public void next() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#play()
     */
    @Override
    public void play() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#playmode(boolean, boolean)
     */
    @Override
    public void playmode(final boolean shuffle, final boolean repeat) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#prev()
     */
    @Override
    public void prev() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#refreshZoneAttributes()
     */
    @Override
    public void refreshZoneAttributes() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#remove(java.lang.String)
     */
    @Override
    public void remove(final String id) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#removeAll()
     */
    @Override
    public void removeAll() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#save(java.lang.String, java.lang.String)
     */
    @Override
    public void save(final String name, final String uri) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#seekTrack(int)
     */
    @Override
    public void seekTrack(final int nr) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#setCrossfade(boolean)
     */
    @Override
    public void setCrossfade(final boolean b) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#setMute(boolean)
     */
    @Override
    public void setMute(final boolean mute) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#setTransportURI(java.lang.String)
     */
    @Override
    public void setTransportURI(final String uri) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#stop()
     */
    @Override
    public void stop() {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#trace_browse(boolean)
     */
    @Override
    public void trace_browse(final boolean x) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#trace_io(boolean)
     */
    @Override
    public void trace_io(final boolean x) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#trace_reply(boolean)
     */
    @Override
    public void trace_reply(final boolean x) {

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#volume()
     */
    @Override
    public int volume() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.tensin.sonos.ISonos#volume(int)
     */
    @Override
    public void volume(final int vol) {

    }

}
